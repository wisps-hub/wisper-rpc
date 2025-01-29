package com.wisper.client.circuitBreaker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakerProvider {
    private Map<String, SimpleCircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    public synchronized SimpleCircuitBreaker getCircuitBreaker(String serviceName) {
        return circuitBreakerMap.computeIfAbsent(serviceName,
                key-> new SimpleCircuitBreaker(1, 0.5, 10000));
    }

}