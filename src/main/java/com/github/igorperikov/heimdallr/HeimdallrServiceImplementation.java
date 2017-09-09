package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.HeimdallrServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class HeimdallrServiceImplementation extends HeimdallrServiceGrpc.HeimdallrServiceImplBase {
    private final HeimdallrNode node;

    private final ClusterStateMerger merger = new ClusterStateMerger();
    private final ClusterDiffCalculator resolver = new ClusterDiffCalculator();

    @Override
    public void getDiffWithOtherNodesState(
            ClusterStateTO request,
            StreamObserver<ClusterStateDiffTO> responseObserver
    ) {
        log.info("Other node sent her cluster state and asking for a merged state");
        ClusterStateDiffTO diff = resolver.calculate(node.getClusterState(), request);
        node.setClusterState(merger.merge(node.getClusterState(), diff));

        responseObserver.onNext(diff);
        responseObserver.onCompleted();
    }
}
