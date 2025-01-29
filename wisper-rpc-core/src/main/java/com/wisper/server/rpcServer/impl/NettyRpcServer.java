package com.wisper.server.rpcServer.impl;

import com.wisper.server.netty.initializer.NettyServerInitializer;
import com.wisper.server.provider.ServerProvider;
import com.wisper.server.rpcServer.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyRpcServer implements RpcServer {

    private ServerProvider serverProvider;
    private ChannelFuture channelFuture;

    public NettyRpcServer(ServerProvider serverProvider) {
        this.serverProvider = serverProvider;
    }

    @Override
    public void start(int port) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        System.out.println("[netty]启动服务器");
        try {
            //初始化服务
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serverProvider));
            //绑定端口并阻塞
            channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("[netty]绑定端口成功");

            //阻塞并监听关闭事件
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("[netty]服务器启动异常中断: " + e.getCause().getMessage());
        } finally {
            boss.shutdownGracefully().syncUninterruptibly();
            work.shutdownGracefully().syncUninterruptibly();
            System.out.println("[netty]服务器关闭");
        }
    }

    @Override
    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        System.out.println("[netty]启动服务器");
        try {
            //初始化服务
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serverProvider));
            //绑定端口并阻塞
            channelFuture = serverBootstrap.bind(serverProvider.getPort()).sync();
            System.out.println(String.format("[netty]绑定端口成功: %s", serverProvider.getPort()));

            //阻塞并监听关闭事件
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("[netty]服务器启动异常中断: " + e.getCause().getMessage());
        } finally {
            boss.shutdownGracefully().syncUninterruptibly();
            work.shutdownGracefully().syncUninterruptibly();
            System.out.println("[netty]服务器关闭");
        }
    }

    @Override
    public void stop() {
        if (channelFuture != null) {
            try {
                channelFuture.channel().close().sync();
                System.out.println("[netty]服务器主通道关闭");
            }catch (Exception e){
                Thread.currentThread().interrupt();
                System.out.println(String.format("[netty]服务器主通道关闭异常: %s", e.getCause().getMessage()));
            }
        }else {
            System.out.println("[netty]服务器主通道未初始化");
        }
    }
}
