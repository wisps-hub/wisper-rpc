package com.wisper.server.ratelimit.provider;


import com.wisper.server.ratelimit.limiter.RateLimiter;
import com.wisper.server.ratelimit.limiter.TokenBucketRateLimiterImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitProvider {
    //k: 接口全限定名  v:限流器
    private Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    public RateLimiter getRateLimiter(String serviceName) {
        return rateLimiterMap.computeIfAbsent(serviceName,
                k -> new TokenBucketRateLimiterImpl(1000, 2));
    }
}
