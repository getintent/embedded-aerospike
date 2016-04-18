package com.getintent.aerospike.embedded;

public interface DockerMachineEnvironment {

    String getDockerHost();

    String getDockerCertPath();

    String getDockerTlsVerify();

}
