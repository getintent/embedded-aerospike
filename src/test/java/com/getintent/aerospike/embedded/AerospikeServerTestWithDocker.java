package com.getintent.aerospike.embedded;

import com.aerospike.client.AerospikeClient;
import com.getintent.aerospike.client.SimpleAerospikeClient;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.common.net.HostAndPort;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class AerospikeServerTestWithDocker {
    protected AerospikeServer aerospikeServer;
    protected SimpleAerospikeClient aerospikeClient;

    @BeforeMethod
    public void setUp() throws Exception {
        aerospikeServer = AerospikeServer.builder()
                .aerospikeConfPath(getClass().getResource("/aerospike.conf").getFile())
                .dockerConfig(DockerClientConfig.createDefaultConfigBuilder().build())
                .build();
        aerospikeServer.start();
        HostAndPort hostAndPort = aerospikeServer.getHostAndPort();
        aerospikeClient = new SimpleAerospikeClient(
                new AerospikeClient(hostAndPort.getHostText(), hostAndPort.getPort())
        );
    }

    @Test
    public void test() {
        long userId = ThreadLocalRandom.current().nextLong();

        aerospikeClient.addSegments(userId, new HashSet<Integer>() {{
            add(150);
            add(151);
        }});
        Set<Integer> segments = aerospikeClient.getSegments(userId);
        Assert.assertEquals(segments.size(), 2);
        Assert.assertTrue(segments.contains(150));
        Assert.assertTrue(segments.contains(151));

        aerospikeClient.addSegments(userId, new HashSet<Integer>() {{
            add(150);
            add(152);
        }});
        segments = aerospikeClient.getSegments(userId);
        Assert.assertEquals(segments.size(), 3);
        Assert.assertTrue(segments.contains(150));
        Assert.assertTrue(segments.contains(151));
        Assert.assertTrue(segments.contains(152));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        aerospikeServer.stop();
    }

}