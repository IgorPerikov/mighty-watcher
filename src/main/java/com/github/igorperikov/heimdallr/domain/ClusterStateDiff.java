package com.github.igorperikov.heimdallr.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ClusterStateDiff {
    @Getter
    private final Map<String, NodeDefinition> nodes = new HashMap<>();

    public ClusterStateDiff(Map<String, NodeDefinition> nodes) {
        this.nodes.putAll(nodes);
    }
}
