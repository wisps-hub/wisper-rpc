package com.wisper.client.serviceCenter.balance.impl;


import com.wisper.client.serviceCenter.balance.LoadBalance;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡 轮询
 */
public class RoundLoadBalance implements LoadBalance {
    private AtomicInteger choose = new AtomicInteger(0);
    private final List<String> addressNodes = new CopyOnWriteArrayList<>();

    @Override
    public String balance(List<String> addressList) {
        if (CollectionUtils.isEmpty(addressList)) {
            throw new RuntimeException("服务不存在");
        }

        int nodeIdx = choose.getAndUpdate(i -> (i + 1) % addressList.size());
        String address = addressList.get(nodeIdx);
        System.out.println(String.format("轮询负载: %s号机器[%s]", choose, address));
        return address;
    }

    @Override
    public void addNode(String node) {
        addressNodes.add(node);
        System.out.println(String.format("轮询负载新增节点: [%s]", node));
    }

    @Override
    public void delNode(String node) {
        addressNodes.remove(node);
        System.out.println(String.format("轮询负载删除节点: [%s]", node));
    }
}
