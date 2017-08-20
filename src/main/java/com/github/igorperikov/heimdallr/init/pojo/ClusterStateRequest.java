package com.github.igorperikov.heimdallr.init.pojo;

import java.io.Serializable;

public class ClusterStateRequest implements Serializable {
    private final NodeDefinition nodeDefinition;

    public ClusterStateRequest(NodeDefinition nodeDefinition) {
        this.nodeDefinition = nodeDefinition;
    }

    public NodeDefinition getNodeDefinition() {
        return nodeDefinition;
    }
}
