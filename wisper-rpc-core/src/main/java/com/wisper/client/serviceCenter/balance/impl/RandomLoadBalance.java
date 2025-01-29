package com.wisper.client.serviceCenter.balance.impl;


import com.wisper.client.serviceCenter.balance.LoadBalance;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 负载均衡 随机
 */
public class RandomLoadBalance implements LoadBalance {

    private final List<String> addressNodes = new CopyOnWriteArrayList<>();

    @Override
    public String balance(List<String> addressList) {
        if (CollectionUtils.isEmpty(addressList)){
            throw new RuntimeException("服务不存在");
        }
        int choose = (int) (Math.random() * addressList.size());
        String address = addressList.get(choose);
        System.out.println(String.format("随机负载: %s号机器[%s]", choose, address));
        return address;
    }

    @Override
    public void addNode(String node) {
        addressNodes.add(node);
        System.out.println(String.format("随机负载新增节点: [%s]", node));
    }

    @Override
    public void delNode(String node) {
        addressNodes.remove(node);
        System.out.println(String.format("随机负载删除节点: [%s]", node));
    }
}
