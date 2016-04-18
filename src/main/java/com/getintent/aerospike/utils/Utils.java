package com.getintent.aerospike.utils;

import com.google.common.base.Throwables;

import java.net.ServerSocket;


public final class Utils {

    /**
     * Find free port
     *
     * @return
     */
    public final static int findFreePort() {
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
