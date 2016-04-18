package com.getintent.aerospike.client;

import java.nio.ByteBuffer;

public final class Utils {
    public final static int bytesToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = offset; i < 4 + offset; i++) {
            value += ((int) b[i] & 0xff) << (8 * (3 - i));
        }
        return value;
    }

    public final static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(x);
        return buffer.array();
    }
}
