package com.wisper.server.serviceRegister.impl;

import com.wisper.common.utils.ZKUtils;
import com.wisper.server.serviceRegister.ServiceRegister;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;

public class ZKServiceRegister implements ServiceRegister {
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            ZKUtils.createParentsIfNeeded(CreateMode.PERSISTENT, serviceName);
            // 临时节点，服务器下线就删除节点
            ZKUtils.createParentsIfNeeded(CreateMode.EPHEMERAL,
                    serviceName +"/"+ getServiceAddress(serviceAddress));
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress, boolean retry) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            ZKUtils.createParentsIfNeeded(CreateMode.PERSISTENT, serviceName);
            // 临时节点，服务器下线就删除节点
            ZKUtils.createParentsIfNeeded(CreateMode.EPHEMERAL,
                    serviceName +"/"+ getServiceAddress(serviceAddress));
            if (retry){
                //加入重试白名单
                ZKUtils.addRetryWhiteList(serviceName);
            }
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }
    }

    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }
}
