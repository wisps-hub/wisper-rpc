package com.wisper.client.netty.handler;

import com.wisper.common.message.RpcRespMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcRespMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRespMsg msg) throws Exception {
        System.out.println("[clientHandler]请求结果: " + msg);
        //将resp对象 绑定到当前channel上 方便后边使用
        AttributeKey<Object> rpcRespKey = AttributeKey.valueOf("rpcResp");
        ctx.channel().attr(rpcRespKey).set(msg);
        //关闭当前channel (短链接模式)
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("[clientHandler]远程调用异常: " + cause.getMessage());
        ctx.close();
    }
}