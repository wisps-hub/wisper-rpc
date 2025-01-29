package com.wisper.server.provider;

import com.wisper.common.config.ProtocolConfig;
import com.wisper.server.ratelimit.provider.RateLimitProvider;
import com.wisper.server.serviceRegister.ServiceRegister;
import com.wisper.server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerProvider {
    private String host;
    private int port;
    private ServiceRegister serviceRegister;
    private RateLimitProvider rateLimitProvider;

    // 本地服务实例Map
    // k 接口的全限定名, v 接口对应的实例
    private Map<String, Object> LocalServiceMap;

    @Deprecated
    public ServerProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.serviceRegister = new ZKServiceRegister();
        this.LocalServiceMap = new HashMap<>();
        this.rateLimitProvider = new RateLimitProvider();
    }

    public ServerProvider() {
        this.host = ProtocolConfig.host;
        this.port = ProtocolConfig.port;
        this.serviceRegister = ServiceRegister.getInstance();
        this.LocalServiceMap = new HashMap<>();
        this.rateLimitProvider = new RateLimitProvider();
    }

    //注册服务
    public void registService(Object service, boolean retry){
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            LocalServiceMap.put(clazz.getName(), service);
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port), retry);
        }
    }

    //获取服务实例
    public Object getService(String interfaceName){
        return LocalServiceMap.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider() {
        return rateLimitProvider;
    }

    public int getPort() {
        return port;
    }
}
