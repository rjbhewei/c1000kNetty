package com.c1000k.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hewei
 * @version 5.0
 * @date 16/4/18  21:12
 * @desc
 */
public class Netty5C1000kServer {

    private static final Logger logger = LoggerFactory.getLogger(Netty5C1000kServer.class);

    //private static final int PORT = 8080;

    public void start(String localIp, int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            bootstrap.option(ChannelOption.SO_TIMEOUT, 10);
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            bootstrap.childHandler(new WebSocketServerInitializer());
            Channel ch = bootstrap.bind(localIp, port).sync().channel();
            logger.info("web socket port:{}", port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if(args == null || args.length != 2) {
            return;
        }
        String localIp = args[0];
        int port = Integer.parseInt(args[1]);
        new Netty5C1000kServer().start(localIp,port);
    }
}
