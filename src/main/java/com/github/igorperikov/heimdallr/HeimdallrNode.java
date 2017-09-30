package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.epidemics.RandomNodeAntiEntropyMechanism;
import com.github.igorperikov.heimdallr.generated.Type;
import com.github.igorperikov.heimdallr.grpc.client.HeimdallrServiceClient;
import com.github.igorperikov.heimdallr.grpc.server.HeartbeatServiceImplementation;
import com.github.igorperikov.heimdallr.grpc.server.HeimdallrServiceImplementation;
import com.github.igorperikov.heimdallr.storage.HeimdallrStorage;
import com.github.igorperikov.heimdallr.storage.HeimdallrStorageMapImplementation;
import com.github.igorperikov.heimdallr.storage.HeimdallrStorageValue;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
public class HeimdallrNode {
    private final int port;
    private final int apiPort;

    @Getter
    private final UUID label;

    private final HeimdallrStorage storage;

    @Getter
    private ClusterState clusterState;

    private String peerNodeAddress;
    private Integer peerNodePort;

    public HeimdallrNode(int apiPort, int port) {
        this.apiPort = apiPort;
        this.port = port;
        this.label = UUID.randomUUID();
        this.storage = new HeimdallrStorageMapImplementation();
    }

    public HeimdallrNode(int apiPort, int port, String peerAddress, int peerPort) {
        this(apiPort, port);
        this.peerNodeAddress = peerAddress;
        this.peerNodePort = peerPort;
    }

    public void start() {
        setupPersonalClusterState();
        setupApi();

        Server server = ServerBuilder.forPort(port)
                .addService(new HeimdallrServiceImplementation(this))
                .addService(new HeartbeatServiceImplementation())
                .build();

        if (peerNodeAddress != null && peerNodePort != null) {
            log.info("Peer node {}:{} is provided", peerNodeAddress, peerNodePort);
            ClusterStateDiff diff = new HeimdallrServiceClient(peerNodeAddress, peerNodePort)
                    .getClusterStateDiff(getClusterState());
            clusterState.applyDiff(diff);
        }

        try {
            server.start();
            log.info("{} start and listening on {}", label, port);
        } catch (IOException e) {
            log.error("", e);
            System.exit(1);
        }

        ScheduledFuture antiEntropyFuture = new RandomNodeAntiEntropyMechanism(this).launch();
        ScheduledFuture clusterInfoFuture = new ClusterInfoLogger(this).launch();
        ScheduledFuture heartbeatFuture = new HeartbeatMechanism(this).launch();

        try {
            server.awaitTermination();
            antiEntropyFuture.cancel(true);
            clusterInfoFuture.cancel(true);
            heartbeatFuture.cancel(true);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void setupApi() {
        HandlerFunction<ServerResponse> getValueHandlerFunction = request -> {
            return storage.get(request.pathVariable("key"))
                    .flatMap(stringValue -> ServerResponse.ok()
                            .contentType(APPLICATION_JSON)
                            .body(
                                    Mono.just(new HeimdallrStorageValue(stringValue)),
                                    HeimdallrStorageValue.class
                            )
                    ).switchIfEmpty(ServerResponse.notFound().build());
        };

        HandlerFunction<ServerResponse> putValueHandlerFunction = request -> {
            return request.bodyToMono(HeimdallrStorageValue.class)
                    .flatMap(requestBody -> storage.put(request.pathVariable("key"), requestBody.getValue()))
                    .flatMap(aVoid -> ServerResponse.ok().build());
        };

        HandlerFunction<ServerResponse> deleteValueHandlerFunction = request -> {
            return storage.remove(request.pathVariable("key")).flatMap(aVoid -> ServerResponse.noContent().build());
        };

        RouterFunction<ServerResponse> route = RouterFunctions
                .route(RequestPredicates.GET("/value/{key}"), getValueHandlerFunction)
                .andRoute(
                        RequestPredicates.PUT("/value/{key}").and(RequestPredicates.accept(APPLICATION_JSON)),
                        putValueHandlerFunction
                )
                .andRoute(RequestPredicates.DELETE("/value/{key}"), deleteValueHandlerFunction);

        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer.create(apiPort).newHandler(adapter).block();
        log.info("Client api is now listening on {}", apiPort);
    }

    private void setupPersonalClusterState() {
        clusterState = new ClusterState(label.toString(), getNodeAddress());
    }

    public String getNodeAddress() {
        // TODO: localhost -> ip detection
        return "localhost:" + port;
    }

    public List<NodeDefinition> getOtherLiveNodeDefinitions() {
        return clusterState.getNodes().values().stream()
                .filter(node -> node.getType() == Type.LIVE)
                .filter(node -> !node.getLabel().equals(label.toString()))
                .collect(Collectors.toList());
    }
}
