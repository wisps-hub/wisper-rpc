package com.wisper.server.ratelimit.limiter;

public class TokenBucketRateLimiterImpl implements RateLimiter {
    //桶容量
    private final int capacity;
    //令牌生产速率 (单位ms)
    private final int rate;
    //当前桶容量
    private volatile int currCapacity;
    //上次请求时间戳
    private volatile long lastTimeStamp;

    public TokenBucketRateLimiterImpl(int rate, int capacity) {
        this.rate = rate;
        this.capacity = capacity;
        this.currCapacity = capacity;
        this.lastTimeStamp = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean getToken() {
        //有剩余令牌
        if (currCapacity > 0) {
            currCapacity--;
            return true;
        }
        //无剩余令牌
        long current = System.currentTimeMillis();
        //如果距离上一次的请求的时间大于RATE的时间
        if (current - lastTimeStamp >= rate) {
            //计算这段时间间隔中生成的令牌数, 如果>2, 桶容量加上（计算的令牌-1）
            if ((current - lastTimeStamp) / rate >= 2) {
                currCapacity += (int) (current - lastTimeStamp) / rate - 1;
            }
            //保持桶内令牌容量<=CAPACITY
            currCapacity = Math.min(currCapacity, capacity);
            //刷新时间戳为本次请求
            lastTimeStamp = current;
            return true;
        }
        //获得不到，返回false
        return false;
    }
}
