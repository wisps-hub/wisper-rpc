package com.wisper.client.netty.initializer;

import com.wisper.client.netty.handler.NettyClientHandler;
import com.wisper.common.protocol.coder.MessageCodec;
import com.wisper.common.protocol.coder.ProtocolFrameDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //帧解码器 解决粘包 半包问题
        pipeline.addLast(new ProtocolFrameDecoder());
        //自定义编解码器
        pipeline.addLast(new MessageCodec());
        //pipeline.addLast(new ReadTimeoutHandler(3));//模拟重试机制
        //处理服务端响应
        pipeline.addLast(new NettyClientHandler());
    }
}
