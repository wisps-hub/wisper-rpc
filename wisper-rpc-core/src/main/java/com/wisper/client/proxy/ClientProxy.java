package com.wisper.client.proxy;

import com.wisper.client.circuitBreaker.CircuitBreakerProvider;
import com.wisper.client.circuitBreaker.SimpleCircuitBreaker;
import com.wisper.client.serviceCenter.ServiceCenter;
import com.wisper.client.retry.GuavaRetry;
import com.wisper.client.rpcClient.RpcClient;
import com.wisper.client.rpcClient.impl.NettyRpcClient;
import com.wisper.client.serviceCenter.ZkServiceCenter;
import com.wisper.common.message.RpcReqMsg;
import com.wisper.common.message.RpcRespMsg;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;
    private ServiceCenter serviceCenter;
    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy() {
        this.serviceCenter = new ZkServiceCenter();
        this.rpcClient = new NettyRpcClient(serviceCenter);
        circuitBreakerProvider = new CircuitBreakerProvider();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(">>>>>>>>代理方法执行");
        RpcReqMsg rpcReqMsg = RpcReqMsg.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .build();
        //获取熔断器
        SimpleCircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if (!circuitBreaker.tryRequest()) {
            return null;
        }
        RpcRespMsg rpcRespMsg;
        if (serviceCenter.checkRetry(rpcReqMsg.getInterfaceName())) {
            //白名单内的接口 进行重试
            rpcRespMsg = new GuavaRetry(rpcClient).sendMsgRetry(rpcReqMsg);
        } else {
            //白名单外的接口 只调用一次
            rpcRespMsg = rpcClient.sendMsg(rpcReqMsg);
        }
        if (rpcRespMsg == null) {
            //请求失败上报
            circuitBreaker.recordFail();
            return null;
        }
        if (rpcRespMsg.getCode() == 200) {
            //请求成功上报
            circuitBreaker.recordSuccess();
        }
        if (rpcRespMsg.getCode() == 500) {
            //请求失败上报
            circuitBreaker.recordFail();
        }
        return rpcRespMsg.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
