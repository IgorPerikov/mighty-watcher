package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.init.pojo.NodeDefinition;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ClusterInfoLogger {
    public ScheduledFuture<?> startPrintingClusterInfo(EventLoop eventLoop, HeimdallrNode heimdallrNode) {
        return eventLoop.scheduleWithFixedDelay(
                () -> {
                    String clusterStateString = heimdallrNode.getClusterNodes()
                            .stream()
                            .map(NodeDefinition::toString)
                            .collect(Collectors.joining("\r\n"));
                    log.info("Cluster state: \r\n[{}]", clusterStateString);
                },
                5,
                10,
                TimeUnit.SECONDS
        );
    }
}
