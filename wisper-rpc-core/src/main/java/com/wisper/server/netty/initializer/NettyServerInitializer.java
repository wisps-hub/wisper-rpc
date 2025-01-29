package com.wisper.server.netty.initializer;

import com.wisper.common.protocol.coder.MessageCodec;
import com.wisper.common.protocol.coder.ProtocolFrameDecoder;
import com.wisper.server.netty.handler.NettyServerHandler;
import com.wisper.server.provider.ServerProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private ServerProvider serverProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //帧解码器 解决粘包 半包问题
        pipeline.addLast(new ProtocolFrameDecoder());
        //自定义编解码器
        pipeline.addLast(new MessageCodec());
        //处理客户端请求
        pipeline.addLast(new NettyServerHandler(serverProvider));
    }
}
