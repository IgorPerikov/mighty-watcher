package com.github.igorperikov.heimdallr;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ClusterInfoLogger {
    private static final Logger log = LoggerFactory.getLogger(ClusterInfoLogger.class);

    public ScheduledFuture<?> startPrintingCLusterInfo(EventLoop eventLoop, HeimdallrNode heimdallrNode) {
        return eventLoop.scheduleWithFixedDelay(
                () -> {
                    log.info("Cluster state: {}", heimdallrNode.getClusterNodes());
                },
                15,
                15,
                TimeUnit.SECONDS
        );
    }
}
