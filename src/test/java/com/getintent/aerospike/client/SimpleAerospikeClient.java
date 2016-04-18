package com.getintent.aerospike.client;

import com.aerospike.client.*;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleAerospikeClient {
    private static final String SEGMENTS_NAMESPACE = "S";
    private static final String SEGMENTS_SET = "segments";
    private static final String SEGMENTS_BIN = "segment";

    private final AerospikeClient client;
    private final Policy readPolicy;
    private final WritePolicy writePolicy;


    public SimpleAerospikeClient(AerospikeClient client) {
        this.client = client;
        this.readPolicy = new Policy();
        this.writePolicy = new WritePolicy();
    }

    public Set<Integer> getSegments(Long userId) {
        Objects.requireNonNull(userId, "User ID can't be null!");
        Key key;
        key = segmentKey(userId);
        Record r = client.get(readPolicy, key);
        if (r == null) {
            return Collections.emptySet();
        }
        return byteListToIntSet((List<byte[]>) r.getValue(SEGMENTS_BIN));

    }

    public void addSegments(Long userId, Set<Integer> segments) {
        Objects.requireNonNull(userId, "User ID can't be null!");
        if (segments == null)
            return;
        Key key = segmentKey(userId);
        Record r = client.get(readPolicy, key);
        Value[] segValues;
        if (r == null) {
            segValues = segments.stream().map(i -> Value.get(Utils.intToBytes(i))).toArray(Value[]::new);
        } else {
            Set<Integer> interim = byteListToIntSet((List<byte[]>) r.getValue(SEGMENTS_BIN));
            interim.addAll(segments);
            segValues = interim.stream().map(i -> Value.get(Utils.intToBytes(i))).toArray(Value[]::new);
        }

        client.put(writePolicy, key, new Bin(SEGMENTS_BIN, Value.get(segValues)));
    }

    private Set<Integer> byteListToIntSet(List<byte[]> list) {
        if (list != null && list.size() > 0) {
            return list.stream().map(b -> Utils.bytesToInt(b, 0)).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    private static Key segmentKey(Long id) {
        return new Key(SEGMENTS_NAMESPACE, SEGMENTS_SET, id);
    }
}
