package com.github.igorperikov.heimdallr;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class ClusterInfoLogger {
    private final int clusterInfoLoggingDelayInSeconds;
    private final HeimdallrNode node;

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                () -> {
                    String clusterStateString = node.getClusterState().toString();
                    log.info("Cluster state: \r\n[{}]", clusterStateString);
                },
                clusterInfoLoggingDelayInSeconds,
                clusterInfoLoggingDelayInSeconds,
                TimeUnit.SECONDS
        );
    }
}
