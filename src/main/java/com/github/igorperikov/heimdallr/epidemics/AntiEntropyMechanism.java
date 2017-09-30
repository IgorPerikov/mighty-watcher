package com.github.igorperikov.heimdallr.epidemics;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.grpc.client.HeimdallrServiceClient;
import io.grpc.StatusRuntimeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public abstract class AntiEntropyMechanism {
    private final int antiEntropyDelayInSeconds;
    private final HeimdallrNode currentNode;
    private final HeimdallrServiceClient client = new HeimdallrServiceClient();

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                () -> {
                    List<NodeDefinition> otherNodes = currentNode.getOtherLiveNodeDefinitions();
                    if (otherNodes.isEmpty()) {
                        log.info("No other nodes in cluster");
                    } else {
                        NodeDefinition node = chooseNode(otherNodes);
                        try {
                            send(node);
                        } catch (StatusRuntimeException e) {
                            log.warn("Anti entropy call has failed");
                            currentNode.getClusterState().markWithTombstone(node.getLabel());
                        }
                    }
                },
                antiEntropyDelayInSeconds,
                antiEntropyDelayInSeconds,
                TimeUnit.SECONDS
        );
    }

    private void send(NodeDefinition node) {
        Integer port = Integer.valueOf(node.getAddress().split(":")[1]);
        String address = "localhost";
        log.info("Sending anti entropy request to {}:{}", address, port);
        ClusterStateDiff diff = client.getClusterStateDiff(currentNode.getClusterState(), address, port);
        currentNode.getClusterState().applyDiff(diff);
    }

    protected abstract NodeDefinition chooseNode(List<NodeDefinition> nodes);
}
