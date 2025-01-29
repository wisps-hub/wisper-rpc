package com.wisper.client.serviceCenter.cache;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {

    private static Map<String, List<String>> cache = new ConcurrentHashMap<>();

    public void add(String serviceName, String address){
        System.out.println(String.format("[serviceCache]添加本地缓存: %s, %s", serviceName, address) );
        List<String> list = cache.getOrDefault(serviceName, new ArrayList<>());
        list.add(address);
        cache.put(serviceName, list);
        System.out.println(String.format("[serviceCache]本地缓存: %s", cache));
    }

    public void delete(String serviceName, String address){
        System.out.println(String.format("[serviceCache]删除本地缓存: %s, %s", serviceName, address) );
        List<String> list = cache.get(serviceName);
        if (CollectionUtils.isNotEmpty(list)){
            list.remove(address);
        }
        System.out.println(String.format("[serviceCache]本地缓存: %s", cache));
    }

    public void update(String serviceName, String oldAddress, String newAddress){
        List<String> list = cache.get(serviceName);
        if (CollectionUtils.isNotEmpty(list)){
            list.remove(oldAddress);
            list.add(newAddress);
        }
    }

    public List<String> getServiceFromCache(String serviceName){
        if (cache.isEmpty() || !cache.containsKey(serviceName)){
            return Collections.EMPTY_LIST;
        }
        return cache.get(serviceName);
    }

}
