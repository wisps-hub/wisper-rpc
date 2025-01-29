package com.wisper.server.ratelimit.limiter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 令牌桶算法 限流器
 */
public class TokenBucketRateLimiter implements RateLimiter {
    private final int capacity; // 令牌桶的容量
    private final long fillInterval; // 令牌桶的填充间隔时间（毫秒）
    private final int fillAmount; // 每次填充的令牌数量
    private long lastFillTime; // 上次填充令牌的时间
    private int tokens; // 当前令牌桶中的令牌数量
    private final ReentrantLock lock = new ReentrantLock(); // 用于线程安全

    public TokenBucketRateLimiter(int capacity, long fillInterval, int fillAmount) {
        this.capacity = capacity;
        this.fillInterval = fillInterval;
        this.fillAmount = fillAmount;
        this.lastFillTime = System.currentTimeMillis();
        this.tokens = capacity; // 初始化时令牌桶是满的
    }

    @Override
    public boolean getToken() {
        lock.lock();
        System.out.println(tokens);
        try {
            long now = System.currentTimeMillis();
            // 计算自上次填充以来经过的时间
            long elapsed = now - lastFillTime;
            // 如果经过的时间大于或等于填充间隔，则进行填充
            if (elapsed >= fillInterval) {
                // 计算可以填充的令牌数量
                int newTokens = (int) (elapsed / fillInterval) * fillAmount;
                // 更新令牌数量，但不能超过容量
                tokens = Math.min(tokens + newTokens, capacity);
                // 更新上次填充时间
                lastFillTime = now - (elapsed % fillInterval);
            }
            // 如果有令牌，则获取一个令牌并返回 true
            if (tokens > 0) {
                tokens--;
                return true;
            }
            // 如果没有令牌，则返回 false
            return false;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        // 创建一个令牌桶限流器，容量为 10，每 100 毫秒填充 1 个令牌
        RateLimiter rateLimiter = new TokenBucketRateLimiter(10, 100, 1);

        // 模拟请求
        for (int i = 0; i < 20; i++) {
            if (rateLimiter.getToken()) {
                System.out.println("Request " + (i + 1) + " is allowed");
            } else {
                System.out.println("Request " + (i + 1) + " is denied");
            }
            try {
                Thread.sleep(10); // 模拟请求间隔
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
