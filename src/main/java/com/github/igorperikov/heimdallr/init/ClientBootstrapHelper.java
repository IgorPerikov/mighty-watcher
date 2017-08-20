package com.github.igorperikov.heimdallr.init;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class ClientBootstrapHelper {
    public static Bootstrap build(EventLoopGroup eventLoopGroup, InetSocketAddress remote, HeimdallrNode node) {
        return new Bootstrap().group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(remote)
                .handler(new NodeClientChannelInitializer(node));
    }
}
