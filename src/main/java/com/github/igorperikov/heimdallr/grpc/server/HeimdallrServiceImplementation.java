package com.github.igorperikov.heimdallr.grpc.server;

import com.github.igorperikov.heimdallr.ClusterDiffCalculator;
import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.converter.ClusterStateConverter;
import com.github.igorperikov.heimdallr.converter.ClusterStateDiffConverter;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
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

    @Override
    public void getDiffWithOtherNodesState(
            ClusterStateTO request,
            StreamObserver<ClusterStateDiffTO> responseObserver
    ) {
        log.info("Other node sent her cluster state and asking for a merged state");
        ClusterStateTO currentNodeState = ClusterStateConverter.convertDomain(node.getClusterState());

        ClusterStateDiff diff = ClusterDiffCalculator.calculate(
                ClusterStateConverter.convertToDomain(currentNodeState),
                ClusterStateConverter.convertToDomain(request)
        );
        node.getClusterState().applyDiff(diff);

        responseObserver.onNext(ClusterStateDiffConverter.convertDomain(diff));
        responseObserver.onCompleted();
    }
}
