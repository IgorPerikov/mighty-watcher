package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ClusterStateResolver {
    public static ClusterStateTO resolve(ClusterStateTO first, ClusterStateTO second) {
        Map<String, NodeDefinitionTO> map = new HashMap<>(second.getNodesMap());
        for (NodeDefinitionTO def : first.getNodesMap().values()) {
            String label = def.getLabel();
            if (map.containsKey(label)) {
                Instant incomingStateTime = Instant.parse(def.getTimestamp());
                Instant thisStateTime = Instant.parse(map.get(label).getTimestamp());
                if (incomingStateTime.isAfter(thisStateTime)) {
                    map.replace(label, def);
                }
            } else {
                map.put(label, def);
            }
        }

        return ClusterStateTO.newBuilder().putAllNodes(map).build();
    }
}
