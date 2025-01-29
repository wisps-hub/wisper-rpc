package com.wisper.client.rpcClient.impl;

import com.wisper.client.rpcClient.RpcClient;
import com.wisper.client.serviceCenter.ServiceCenter;
import com.wisper.common.message.RpcReqMsg;
import com.wisper.common.message.RpcRespMsg;
import com.wisper.client.netty.initializer.NettyClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class NettyRpcClient implements RpcClient {
    private ServiceCenter serviceCenter;
    private static final Bootstrap bootStrap;
    private static final EventLoopGroup eventLoopGroup;

    public NettyRpcClient(ServiceCenter serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    static {
        //初始化
        eventLoopGroup = new NioEventLoopGroup();
        bootStrap = new Bootstrap();
        bootStrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcRespMsg sendMsg(RpcReqMsg reqMsg) {
        try {
            InetSocketAddress address = serviceCenter.serviceDiscovery(reqMsg.getInterfaceName());
            //sync()阻塞等待建立连接完成
            ChannelFuture channelFuture = bootStrap.connect(address.getHostName(), address.getPort()).sync();
            Channel ch = channelFuture.channel();
            //发送数据
            ch.writeAndFlush(reqMsg);
            //sync()堵塞获取结果
            ch.closeFuture().sync();

            AttributeKey<RpcRespMsg> rpcRespKey = AttributeKey.valueOf("rpcResp");
            RpcRespMsg rpcRespMsg = ch.attr(rpcRespKey).get();
            System.out.println("[client]请求结果: " + rpcRespMsg);
            if (rpcRespMsg == null){
                return RpcRespMsg.fail();
            }
            return rpcRespMsg;
        } catch (Exception e) {
            System.out.println("[client]请求异常" + e.getCause().getMessage());
        }
        return null;
    }
}
