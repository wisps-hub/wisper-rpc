package com.wisper.client.serviceCenter;

import com.wisper.client.serviceCenter.balance.LoadBalance;
import com.wisper.client.serviceCenter.balance.impl.RandomLoadBalance;
import com.wisper.client.serviceCenter.watcher.ZkWatcher;
import com.wisper.common.utils.ZKUtils;
import com.wisper.client.serviceCenter.cache.ServiceCache;
import org.apache.commons.collections4.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;


public class ZkServiceCenter implements ServiceCenter {

    private ServiceCache serviceCache;

    private LoadBalance loadBalance;

    public ZkServiceCenter() {
        //初始化负载均衡器
        loadBalance = LoadBalance.getInstance();
        //初始化缓存
        this.serviceCache = new ServiceCache();
        //监听服务节点 实时刷新本地缓存
        ZkWatcher zkWatcher = new ZkWatcher(serviceCache);
        zkWatcher.watchToUpdate(ZKUtils.ROOT_PATH);
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //走本地缓存
            List<String> services = serviceCache.getServiceFromCache(serviceName);
            if (CollectionUtils.isEmpty(services)){
                //缓存没有去注册中心找
                services = ZKUtils.getChildList(serviceName);
            }
            // 负载均衡
            String addressStr = loadBalance.balance(services);
            return parseAddress(addressStr);
        } catch (Exception e) {
            System.out.println("服务发现异常: " + e.getCause().getMessage());
            throw new RuntimeException("服务发现异常: " + e.getCause().getMessage());
        }
    }

    @Override
    public boolean checkRetry(String serviceName) {
        Set<String> retryWhiteList = ZKUtils.getRetryWhiteList();
        return retryWhiteList.contains(serviceName);
    }

    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }


}
