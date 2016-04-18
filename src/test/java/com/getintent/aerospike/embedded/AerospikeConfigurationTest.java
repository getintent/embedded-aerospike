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
                .dockerMachine(DockerMachine.builder().buildAndStart()).build();
    }
}
