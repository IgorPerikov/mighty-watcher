package com.github.igorperikov.heimdallr.domain;

import com.github.igorperikov.heimdallr.generated.Type;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
// TODO: cluster state taking too much functionality?
public class ClusterState {
    @Getter
    private final Map<String, NodeDefinition> nodes = new HashMap<>();

    public ClusterState(Map<String, NodeDefinition> nodes) {
        this.nodes.putAll(nodes);
    }

    public ClusterState(NodeDefinition nodeDefinition) {
        nodes.put(nodeDefinition.getLabel(), nodeDefinition);
    }

    public ClusterState(String label, String address) {
        nodes.put(label, NodeDefinition.buildLiveDefinition(label, address));
    }

    /**
     * applies diff without any checks
     */
    public void applyDiff(ClusterStateDiff diff) {
        nodes.putAll(diff.getNodes());
    }

    public void markWithTombstone(String label) {
        NodeDefinition nodeDefinition = nodes.get(label);
        if (nodeDefinition == null) {
            log.warn("No node with given label found");
        } else {
            nodeDefinition.setType(Type.TOMBSTONE);
            nodeDefinition.setTimestamp(Instant.now());
        }
    }

    @Override
    public String toString() {
        return nodes.values().stream()
                .map(NodeDefinition::toString)
                .collect(Collectors.joining("\r\n"));
    }
}
