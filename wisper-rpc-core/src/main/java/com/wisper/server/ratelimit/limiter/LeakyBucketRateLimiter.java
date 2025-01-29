package com.wisper.server.ratelimit.limiter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 漏桶算法 限流器
 */
public class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity; // 漏桶的容量
    private final long leakInterval; // 漏桶的漏水间隔时间（毫秒）
    private final int leakAmount; // 每次漏水的量
    private long lastLeakTime; // 上次漏水的时间
    private int tokens; // 当前漏桶中的令牌数量
    private final ReentrantLock lock = new ReentrantLock(); // 用于线程安全

    public LeakyBucketRateLimiter(int capacity, long leakInterval, int leakAmount) {
        this.capacity = capacity;
        this.leakInterval = leakInterval;
        this.leakAmount = leakAmount;
        this.lastLeakTime = System.currentTimeMillis();
        this.tokens = 0; // 初始化时漏桶是空的
    }

    @Override
    public boolean getToken() {
        lock.lock();
        System.out.println(tokens);
        try {
            long now = System.currentTimeMillis();
            // 计算自上次漏水以来经过的时间
            long elapsed = now - lastLeakTime;
            // 如果经过的时间大于或等于漏水间隔，则进行漏水
            if (elapsed >= leakInterval) {
                // 计算可以漏水的次数
                int leaks = (int) (elapsed / leakInterval);
                // 更新漏桶中的令牌数量，但不能小于0
                tokens = Math.max(tokens - leaks * leakAmount, 0);
                // 更新上次漏水时间
                lastLeakTime = now - (elapsed % leakInterval);
            }
            // 如果漏桶中有令牌，则获取一个令牌并返回 true
            if (tokens < capacity) {
                tokens++;
                return true;
            }
            // 如果漏桶中没有令牌，则返回 false
            return false;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        // 创建一个漏桶限流器，容量为 10，每 100 毫秒漏水 1 个令牌
        RateLimiter rateLimiter = new LeakyBucketRateLimiter(10, 100, 1);

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
