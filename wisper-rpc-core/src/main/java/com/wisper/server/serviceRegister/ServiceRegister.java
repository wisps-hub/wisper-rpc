package com.wisper.server.serviceRegister;

import com.wisper.common.config.ProtocolConfig;
import com.wisper.common.enums.RegisterType;
import com.wisper.server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;

// 服务注册接口
public interface ServiceRegister {

    /**
     * 注册服务到注册中心
     *
     * @param serviceName 服务名 com.note.rpc.common.service.UserService
     * @param serviceAddress 服务地址 120.0.0.1:9999
     */
    void register(String serviceName, InetSocketAddress serviceAddress);

    /**
     * 注册服务到注册中心 带重试
     *
     * @param serviceName 服务名 格式: com.note.rpc.common.service.UserService
     * @param serviceAddress 服务地址 格式: 120.0.0.1:9999
     * @param retry 是否重试
     */
    void register(String serviceName, InetSocketAddress serviceAddress, boolean retry);

    static ServiceRegister getInstance(){
        RegisterType registerType = RegisterType.getEnum(ProtocolConfig.registryType);
        switch (registerType){
            case ZOOKEEPER:
                return new ZKServiceRegister();
        }
        return new ZKServiceRegister();
    }
}
