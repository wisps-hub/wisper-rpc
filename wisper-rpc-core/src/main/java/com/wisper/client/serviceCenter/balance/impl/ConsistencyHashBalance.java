package com.wisper.client.serviceCenter.balance.impl;

import com.wisper.client.serviceCenter.balance.LoadBalance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 负载均衡 一致性hash
 * 虚拟节点需要根据业务
 */
public class ConsistencyHashBalance implements LoadBalance {

    //虚拟节点数量
    private static final int V_NODE_NUM = 10;

    //虚拟节点
    private SortedMap<Integer, String> vNodeMap = new TreeMap<>();

    //物理节点
    private List<String> addressNodes = new ArrayList<>();

    //32位hash种子 2166136261
    private static final long FNV_OFFSET_BASIS_32 = 0x811c9dc5L;
    //32位素数系数 16777619
    private static final long FNV_PRIME_32 = 0x01000193L;
    //64位hash种子 14695981039346656037
    private static final long FNV_OFFSET_BASIS_64 = 0xcbf29ce484222325L;
    //64位素数系数 1099511628211
    private static final long FNV_PRIME_64 = 0x100000001b3L;

    private static final String SPILT = "##V";
    @Override
    public String balance(List<String> addressList) {
        if (CollectionUtils.isEmpty(addressList)){
            throw new RuntimeException("服务不存在");
        }
        //todo 模拟一致性哈希  实际业务需要换成业务属性 如用户id 业务id等  可以用ThreadLocal传递
        String businessId = UUID.randomUUID().toString();
        String address = getServer(businessId, addressList);
        System.out.println(String.format("一致性hash负载: 机器[%s]", address));
        return address;
    }

    @Override
    public void addNode(String node) {
        if (StringUtils.isBlank(node) || addressNodes.contains(node)){
            return;
        }
        addressNodes.add(node);
        for (int i = 0; i < V_NODE_NUM; i++) {
            String vAddress = node + SPILT + i;
            int hash = getHash32(vAddress);
            vNodeMap.put(hash, vAddress);
        }
    }

    @Override
    public void delNode(String node) {
        if (StringUtils.isBlank(node) || !addressNodes.contains(node)){
            return;
        }
        addressNodes.remove(node);
        for (int i = 0; i < V_NODE_NUM; i++) {
            String vAddress = node + SPILT + i;
            int hash = getHash32(vAddress);
            vNodeMap.remove(hash);
        }
    }

    private String getServer(String businessId, List<String> addressList) {
        if (CollectionUtils.isEmpty(addressNodes)){
            init(addressList);
        }
        int hash = getHash32(businessId);
        SortedMap<Integer, String> toTailMap = vNodeMap.tailMap(hash);
        String vAddress;
        if (toTailMap.isEmpty()){
            vAddress = vNodeMap.get(vNodeMap.lastKey());
        }else {
            vAddress = vNodeMap.get(toTailMap.firstKey());
        }
        return vAddress.substring(0, vAddress.indexOf(SPILT));
    }

    private void init(List<String> addressList) {
        for (String address : addressList) {
            if (addressNodes.contains(address)) {
                continue;
            }
            addressNodes.add(address);
            for (int i = 0; i < V_NODE_NUM; i++) {
                String vAddress = address + SPILT + i;
                int hash = getHash32(vAddress);
                vNodeMap.put(hash, vAddress);
            }
        }
        System.out.println(String.format("物理节点: %s, 虚拟节点: %s", addressNodes, vNodeMap));

    }

    private int getHash32(String address) {
        //fnv_hash
        int prime = (int) FNV_PRIME_32;
        int hash = (int) FNV_OFFSET_BASIS_32;
        for (int i = 0; i < address.length(); i++){
            hash = (hash ^ address.charAt(i)) * prime;
        }
        //hash扰动
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0){
            hash = Math.abs(hash);
        }
        return hash;
    }

    private long getHash64(String address) {
        //fnv_hash
        long hash = FNV_OFFSET_BASIS_64;
        for (int i = 0; i < address.length(); i++){
            hash = (hash ^ address.charAt(i)) * FNV_PRIME_64;
        }
        //hash扰动
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0){
            hash = Math.abs(hash);
        }
        return hash;
    }

}
