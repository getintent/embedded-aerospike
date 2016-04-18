package com.getintent.aerospike.embedded;

import com.getintent.aerospike.utils.Utils;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Aerospike embedded server built on top of Docker container for Java integration testing.
 */
public class AerospikeServer implements Server {
    private static final Logger LOG = LoggerFactory.getLogger(AerospikeServer.class);
    private DockerClientConfig dockerConfig;
    private DockerClient dockerClient;
    private DockerMachineEnvironment environment;

    private TestDockerCmdExecFactory dockerCmdExecFactory = new TestDockerCmdExecFactory(
            DockerClientBuilder.getDefaultDockerCmdExecFactory()
    );
    private int aerospikePort;
    private final String aerospikeConfPath;
    /**
     * Image name on Docker Hub
     *
     * @see <a href="https://hub.docker.com/_/aerospike/">Official aerospike repository</a>
     */
    public static final String IMAGE_ID = "aerospike";


    public static Builder builder() {
        return new Builder();
    }

    private AerospikeServer(Builder builder) {
        this.aerospikePort = builder.aerospikePort;
        this.aerospikeConfPath = builder.aerospikeConfPath;
        this.dockerConfig = builder.dockerConfig;
        this.environment = builder.environment;
    }

    public HostAndPort getHostAndPort() {
        return HostAndPort.fromParts(
                dockerConfig.getDockerHost().getHost(), aerospikePort
        );
    }

    @Override
    public void start() throws Exception {
        ExposedPort tcp3000 = ExposedPort.tcp(3000);
        Volume volume = new Volume("/etc/aerospike/aerospike.conf");
        Ports portBindings = new Ports();
        portBindings.bind(tcp3000, Ports.binding(aerospikePort));
        setupDockerClient();
        CreateContainerResponse container = dockerClient.createContainerCmd(IMAGE_ID)
                .withExposedPorts(tcp3000).
                        withPortBindings(portBindings)
                .withBinds(new Bind(aerospikeConfPath, volume, AccessMode.ro))
                .exec();

        dockerClient.startContainerCmd(container.getId())
                .exec();

        Executors.newSingleThreadExecutor()
                .submit(new AerospikeContainerMonitor(dockerClient, container.getId()))
                .get(30, TimeUnit.SECONDS);

        LOG.info("Aerospike is up on Port {}", aerospikePort);
    }

    private void setupDockerClient() throws Exception {
        if (environment != null) {
            dockerConfig = DockerClientConfig.createDefaultConfigBuilder()
                    .withDockerCertPath(environment.getDockerCertPath())
                    .withDockerTlsVerify(environment.getDockerTlsVerify())
                    .withDockerHost(environment.getDockerHost()).build();
            dockerClient = DockerClientBuilder.getInstance(this.dockerConfig)
                    .withDockerCmdExecFactory(dockerCmdExecFactory)
                    .build();
        } else {
            dockerClient = DockerClientBuilder.getInstance(dockerConfig)
                    .withDockerCmdExecFactory(dockerCmdExecFactory)
                    .build();
        }
    }

    @Override
    public void stop() throws Exception {
        for (String container : dockerCmdExecFactory.getContainerNames()) {
            LOG.info("Cleaning up temporary container {}", container);
            dockerClient.removeContainerCmd(container).withForce(true).exec();
        }
    }

    public static class Builder {
        private DockerClientConfig dockerConfig;
        private DockerMachineEnvironment environment;
        private int aerospikePort = Utils.findFreePort();
        private String aerospikeConfPath;

        public Builder port(int aerospikePort) {
            Preconditions.checkState(aerospikePort > 0);
            this.aerospikePort = aerospikePort;
            return this;
        }

        public Builder aerospikeConfPath(String aerospikeConfPath) {
            this.aerospikeConfPath = Objects.requireNonNull(aerospikeConfPath);
            return this;
        }

        public Builder dockerConfig(DockerClientConfig dockerConfig) {
            this.dockerConfig = Objects.requireNonNull(dockerConfig);
            return this;
        }

        public Builder dockerMachineEnvironment(DockerMachineEnvironment environment) {
            this.environment = Objects.requireNonNull(environment);
            return this;
        }

        public AerospikeServer build() {
            Preconditions.checkNotNull(aerospikeConfPath, "You should set aerospike configuration file path first");
            if (dockerConfig == null && environment == null) {
                throw new IllegalArgumentException("Either Docker configuration or Docker Machine configuration should be present");
            }
            if (dockerConfig != null && environment != null) {
                throw new IllegalArgumentException("Your should specify only Docker or only Docker Machine configuration");
            }
            return new AerospikeServer(this);
        }
    }
}
