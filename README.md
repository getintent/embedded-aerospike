# embedded-aerospike
Aerospike embedded server built on top of Docker container for Java integration testing.

We use [Java API client for Docker](https://github.com/docker-java/docker-java "Java API client for Docker").

## Configuration

In your application, e.g.

    aerospikeServer = AerospikeServer.builder()
                .aerospikeConfPath(/path/to/aerospike.conf)
                .dockerConfig(DockerClientConfig.createDefaultConfigBuilder().build())
                .build();

AerospikeServer uses DockerClientConfig to build docker client.
There are three ways to configure DockerClientConfig. See [Java API client documentation](https://github.com/docker-java/docker-java/blob/master/README.md "Java API client documentation")

For example, if you use Docker Machine on Mac OS you can put

    DOCKER_HOST=tcp://192.168.99.100:2376
    DOCKER_TLS_VERIFY=1
    DOCKER_CERT_PATH=/home/user/.docker/machine/machines/default
    api.version=1.22

In `$HOME/.docker-java.properties` or in the class path at `/docker-java.properties`

## Usage example in tests

    @BeforeMethod
    public void setUp() throws Exception {
        aerospikeServer = AerospikeServer.builder()
                .aerospikeConfPath("/home/user/aerospike.conf")
                .dockerConfig(DockerClientConfig.createDefaultConfigBuilder().build())
                .build();
        aerospikeServer.start();
    }

    @Test
    public void test() {
        Some tests with Aerospike
    }

    @AfterMethod
    public void tearDown() throws Exception {
        aerospikeServer.stop();
    }
