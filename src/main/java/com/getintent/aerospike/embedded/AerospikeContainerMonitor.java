package com.getintent.aerospike.embedded;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Monitor, check if Aerospike is started and ready inside docker container
 */
public class AerospikeContainerMonitor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AerospikeContainerMonitor.class);

    private final DockerClient docker;
    private final String containerId;
    private boolean isReady;

    public AerospikeContainerMonitor(DockerClient docker, String containerId) {
        this.docker = docker;
        this.containerId = containerId;
    }

    @Override
    public void run() {
        try (LogAerospikeContainerResultCallback loggingCallback = new LogAerospikeContainerResultCallback()) {
            docker.logContainerCmd(containerId).withStdErr(true)
                    .withStdOut(true).withFollowStream(true).exec(loggingCallback);
            while (!isReady && !Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            LOG.error("Error: ", e);
        }
    }

    private class LogAerospikeContainerResultCallback extends LogContainerResultCallback {
        private final StringBuffer log = new StringBuffer();

        @Override
        public void onNext(Frame frame) {
            String nextFrame = new String(frame.getPayload());
            if (nextFrame.contains("cake!")) {
                isReady = true;
            }
            log.append(nextFrame);
            super.onNext(frame);
        }

        @Override
        public String toString() {
            return log.toString();
        }
    }
}
