package com.getintent.aerospike.embedded;

import com.aerospike.client.AerospikeClient;
import com.getintent.aerospike.client.SimpleAerospikeClient;
import com.google.common.net.HostAndPort;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AerospikeServerTestWithDockerMachine extends AerospikeServerTestWithDocker {

    private DockerMachine dockerMachine;

    @BeforeClass
    public void setupMachine() throws Exception {
        dockerMachine = DockerMachine.builder().buildAndStart();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        aerospikeServer = AerospikeServer.builder()
                .aerospikeConfPath(getClass().getResource("/aerospike.conf").getFile())
                .dockerMachineEnvironment(dockerMachine.getEnvironment())
                .build();
        aerospikeServer.start();
        HostAndPort hostAndPort = aerospikeServer.getHostAndPort();
        aerospikeClient = new SimpleAerospikeClient(
                new AerospikeClient(hostAndPort.getHostText(), hostAndPort.getPort())
        );
    }

    @Test(enabled = false)
    @Override
    public void testAerospikeWithDocker() {
        super.testAerospikeWithDocker();
    }
}
