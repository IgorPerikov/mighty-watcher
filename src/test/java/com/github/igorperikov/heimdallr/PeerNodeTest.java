package com.github.igorperikov.heimdallr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeerNodeTest {
    @Test
    void shouldCorrectlyRegisterNewNode() throws InterruptedException {
        HeimdallrNode mainNode = new HeimdallrNode(10000);
        HeimdallrNode secondNode = new HeimdallrNode(10001, "localhost", 10000);
        new Thread(mainNode::start).start();
        new Thread(secondNode::start).start();

        Thread.sleep(1000); // TODO: better way to wait?

        assertEquals(mainNode.getClusterState().getNodes(), secondNode.getClusterState().getNodes());

        assertEquals(2, mainNode.getClusterState().getNodes().size());
    }
}
