package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;

public class ClusterStateDiffApplier {
    // TODO: non-static?
    public static void applyDiff(HeimdallrNode node, ClusterStateDiff diff) {
        if (!diff.getNodes().isEmpty()) {
            node.getClusterState().applyDiff(diff);
        }
    }
}
