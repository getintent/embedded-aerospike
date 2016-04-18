package com.getintent.aerospike.embedded;

import com.github.dockerjava.core.DockerClientConfig;
import org.testng.annotations.Test;

public class AerospikeConfigurationTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testEmptyAerospikeConf() {
        AerospikeServer.builder().build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDoubleConfigs() throws Exception {
        AerospikeServer.builder()
                .aerospikeConfPath(getClass().getResource("/aerospike.conf").getFile())
                .dockerConfig(DockerClientConfig.createDefaultConfigBuilder().build())
                .dockerMachineEnvironment(new DockerMachineEnvironment() {
                    @Override
                    public String getDockerHost() {
                        return null;
                    }

                    @Override
                    public String getDockerCertPath() {
                        return null;
                    }

                    @Override
                    public String getDockerTlsVerify() {
                        return null;
                    }
                }).build();
    }
}
