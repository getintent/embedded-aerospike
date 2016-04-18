package com.getintent.aerospike.embedded;


import com.getintent.exec.CommandExecutor;
import com.getintent.exec.ExecResult;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Docker machine controller
 */
public class DockerMachine {
    private static final Logger LOG = LoggerFactory.getLogger(DockerMachine.class);
    private boolean isAlreadyRunning;
    private final String boxName;
    private final static int[] SUPPORTED_EXIT_CODES = new int[]{0, 1};

    private String dockerHost;
    private String dockerCertPath;
    private String dockerTlsVerify;

    private CommandExecutor commandExecutor;

    private DockerMachine(Builder builder) {
        this.boxName = builder.boxName;
        this.commandExecutor = new CommandExecutor(builder.dockerMachineBinaryPath, SUPPORTED_EXIT_CODES);
    }

    private void checkState() throws IOException {
        ExecResult status = commandExecutor.execute(TimeUnit.SECONDS.toMillis(10), "status", boxName);
        String log = status.logLines().size() == 1 ? status.logLines().get(0) : "";
        Preconditions.checkState(status.getExitCode() == 0, log);
        isAlreadyRunning = log.contains("Running");
        LOG.info("Machine {} {}", boxName, isAlreadyRunning ? "is already running" : "is stopped now");
    }

    public void start() throws IOException {
        checkState();
        if (!isAlreadyRunning) {
            ExecResult start = commandExecutor.execute(TimeUnit.MINUTES.toMillis(1), "start", boxName);
            String log = start.logLines().size() == 1 ? start.logLines().get(0) : "";
            Preconditions.checkState(
                    start.getExitCode() == 0 || log.contains("is already running"), log
            );
        }
        ExecResult env = commandExecutor.execute(TimeUnit.SECONDS.toMillis(10), "env", "--shell=bash", boxName);
        env.logLines().forEach(s -> {
            if (s.startsWith("export") && s.contains(DockerClientConfig.DOCKER_TLS_VERIFY)) {
                dockerTlsVerify = parseValue(s, DockerClientConfig.DOCKER_TLS_VERIFY);
            }
            if (s.startsWith("export") && s.contains(DockerClientConfig.DOCKER_HOST)) {
                dockerHost = parseValue(s, DockerClientConfig.DOCKER_HOST);
            }
            if (s.startsWith("export") && s.contains(DockerClientConfig.DOCKER_CERT_PATH)) {
                dockerCertPath = parseValue(s, DockerClientConfig.DOCKER_CERT_PATH);
            }
        });
    }

    private String parseValue(String s, String name) {
        return s.substring("export ".length() + name.length() + "=\"".length(), s.length() - 1);
    }


    /**
     * Stop machine if it was stopped before {@link #start}
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        if (!isAlreadyRunning) {
            ExecResult result = commandExecutor.execute(TimeUnit.MINUTES.toMillis(1), "stop", boxName);
            Preconditions.checkState(result.getExitCode() == 0, result.logLines().stream().collect(Collectors.joining()));
        }
    }

    public String getDockerHost() {
        return dockerHost;
    }

    public String getDockerCertPath() {
        return dockerCertPath;
    }

    public String getDockerTlsVerify() {
        return dockerTlsVerify;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        /**
         * Default box name in docker-machine, if you haven't default box or want use other you should specify it.
         */
        private static String BOX_NAME = "default";

        /**
         * Assume here that docker-machine is in your path, otherwise you should specify it.
         */
        private static String DOCKER_MACHINE_PATH = "docker-machine";

        private String boxName = BOX_NAME;
        private String dockerMachineBinaryPath = DOCKER_MACHINE_PATH;

        public Builder boxName(String boxName) {
            this.boxName = Objects.requireNonNull(boxName);
            return this;
        }

        public Builder dockerMachineBinaryPath(String dockerMachineBinaryPath) {
            this.dockerMachineBinaryPath = Objects.requireNonNull(dockerMachineBinaryPath);
            return this;
        }

        public DockerMachine buildAndStart() throws Exception {
            DockerMachine dockerMachine = new DockerMachine(this);
            dockerMachine.start();
            return dockerMachine;
        }

    }
}
