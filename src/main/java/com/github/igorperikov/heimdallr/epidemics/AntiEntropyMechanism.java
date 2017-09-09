package com.github.igorperikov.heimdallr.epidemics;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import lombok.AllArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
public abstract class AntiEntropyMechanism {
    private final HeimdallrNode currentNode;

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                    ClusterStateTO clusterState = currentNode.getClusterState();
                    List<NodeDefinitionTO> nodes = clusterState.getNodesMap().entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(currentNode.getLabel().toString()))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    send(chooseNode(nodes));
                },
                15,
                15,
                TimeUnit.SECONDS);
    }

    private void send(NodeDefinitionTO node) {
        Integer port = Integer.valueOf(node.getAddress().split(":")[1]);
        InetSocketAddress address = new InetSocketAddress("localhost", port);
//        try {
//            new InterNodeMessageSender(currentNode, eventLoop, address).send();
//        } catch (InterruptedException ignored) {}
    }

    protected abstract NodeDefinitionTO chooseNode(List<NodeDefinitionTO> nodes);
}
