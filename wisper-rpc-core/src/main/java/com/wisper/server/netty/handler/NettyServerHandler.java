package com.wisper.server.netty.handler;

import com.wisper.common.message.RpcReqMsg;
import com.wisper.common.message.RpcRespMsg;
import com.wisper.server.provider.ServerProvider;
import com.wisper.server.ratelimit.limiter.RateLimiter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;

@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcReqMsg> {

    private ServerProvider serverProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcReqMsg msg) throws Exception {
        System.out.println("[serverHandler]客户端请求: " + msg);
        RpcRespMsg rpcRespMsg = getResp(msg);
        ctx.writeAndFlush(rpcRespMsg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("[serverHandler]服务端异常: " + cause.getMessage());
        ctx.close();
    }

    private RpcRespMsg getResp(RpcReqMsg reqMsg) {
        String interfaceName = reqMsg.getInterfaceName();
        //接口级限流
        RateLimiter rateLimiter = serverProvider.getRateLimitProvider().getRateLimiter(interfaceName);
        if (!rateLimiter.getToken()){
            return RpcRespMsg.fail(500, "服务限流, 稍后再试");
        }
        Object service = serverProvider.getService(interfaceName);
        Method method = null;
        try {
            method = service.getClass().getMethod(reqMsg.getMethodName(), reqMsg.getParamTypes());
            Object invoke = method.invoke(service, reqMsg.getParams());
            System.out.println("[serverHandler]执行结果: " + invoke);
            return RpcRespMsg.success(invoke);
        }catch (Exception e){
            System.out.println("[serverHandler]方法执行失败: " + e.getCause().getMessage());
            return RpcRespMsg.fail();
        }


    }
}
