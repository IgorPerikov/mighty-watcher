package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;

import java.util.HashMap;
import java.util.Map;

public class ClusterStateMerger {
    public ClusterStateTO merge(ClusterStateTO state, ClusterStateDiffTO diff) {
        Map<String, NodeDefinitionTO> nodes = new HashMap<>(state.getNodesMap());
        nodes.putAll(diff.getNodesMap());
        return ClusterStateTO.newBuilder().putAllNodes(nodes).build();
    }
}
