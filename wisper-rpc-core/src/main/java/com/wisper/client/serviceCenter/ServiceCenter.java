package com.wisper.client.serviceCenter;

import java.net.InetSocketAddress;

// 服务发现接口
public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(String serviceName);

    boolean checkRetry(String serviceName) ;
}
