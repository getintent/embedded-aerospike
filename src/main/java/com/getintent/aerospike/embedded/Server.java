package com.getintent.aerospike.embedded;


import com.google.common.net.HostAndPort;

public interface Server {
    /**
     * @return host and port, where server is started
     */
    HostAndPort getHostAndPort();

    /**
     * Start the server.
     *
     * @throws Exception In case starting fails.
     */
    void start() throws Exception;

    /**
     * Stop the server.
     *
     * @throws Exception In case stopping fails.
     */
    void stop() throws Exception;
}
