package com.github.igorperikov.heimdallr.init.pojo;

import java.io.Serializable;
import java.util.Set;

public class ClusterState implements Serializable {
    private final Set<NodeDefinition> nodes;

    public ClusterState(Set<NodeDefinition> nodes) {
        this.nodes = nodes;
    }

    public Set<NodeDefinition> getNodes() {
        return nodes;
    }
}
