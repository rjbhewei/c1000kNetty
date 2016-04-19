package com.c1000k.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hewei
 * @version 5.0
 * @date 16/4/18  21:17
 * @desc
 */
public class Netty5C1000kClient {

    static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");

    //public static final ExecutorService EXECUTOR_SERVICE= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) throws Exception {
        Set<ChannelFuture> futures=new HashSet<>();
        EventLoopGroup group = new NioEventLoopGroup();
        for(int i = 0; i < 5; i++) {
            futures.add(new Netty5C1000kClient().start("192.168.18.101", group));
        }

        for(ChannelFuture future:futures){
             future.sync();
        }

        group.shutdownGracefully();
    }

    public ChannelFuture start(String ip, EventLoopGroup group) throws Exception {
        URI uri = new URI(URL);
        final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), handler);
            }
        }).localAddress(ip, 0);
        Channel ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();
        return ch.closeFuture();
    }
}
