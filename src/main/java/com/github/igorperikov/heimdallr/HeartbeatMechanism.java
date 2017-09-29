package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.grpc.client.HeartbeatServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public class HeartbeatMechanism {
    private HeimdallrNode node;

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                () -> {
                    List<NodeDefinition> otherNodes = node.getOtherLiveNodeDefinitions();
                    for (NodeDefinition nodeDefinition : otherNodes) {
                        Integer port = Integer.valueOf(nodeDefinition.getAddress().split(":")[1]);
                        // TODO: localhost
                        String address = "localhost";
                        log.info("Sending heartbeat request to localhost:{}", port);
                        HeartbeatServiceClient client = new HeartbeatServiceClient(address, port);
                        try {
                            client.call();
                            log.info("Heartbeat to {}:{} was successful", address, port);
                        } catch (Exception e) {
                            log.warn("Heartbeat to {}:{} failed", address, port);
                            node.getClusterState().markWithTombstone(nodeDefinition.getLabel());
                        }
                    }
                },
                5,
                5,
                TimeUnit.SECONDS
        );
    }
}
