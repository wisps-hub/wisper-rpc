package com.wisper.client.circuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleCircuitBreaker {
    //失败阈值
    private final int FAILURE_THRESHOLD;
    //半开启
    private final double HALF_OPEN_SUCCESS_RATE;
    //重试间隔
    private final long TIMEOUT;

    private CircuitState state = CircuitState.CLOSED;
    private AtomicInteger failCnt = new AtomicInteger(0);
    private AtomicInteger successCnt = new AtomicInteger(0);
    private AtomicInteger reqCnt = new AtomicInteger(0);
    private long lastFailTime = 0;

    public SimpleCircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long timeout) {
        this.FAILURE_THRESHOLD = failureThreshold;
        this.HALF_OPEN_SUCCESS_RATE = halfOpenSuccessRate;
        this.TIMEOUT = timeout;
    }

    //查看当前熔断器是否允许请求通过
    public synchronized boolean tryRequest() {
        long currentTime = System.currentTimeMillis();
        System.out.println(String.format("[CircuitBreaker]熔断之前, failureNum: %s", failCnt));
        switch (state) {
            case OPEN:
                if (currentTime - lastFailTime > TIMEOUT) {
                    state = CircuitState.HALF_OPEN;
                    resetCounts();
                    return true;
                }
                System.out.println("[CircuitBreaker]服务熔断");
                return false;
            case HALF_OPEN:
                reqCnt.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    //记录成功
    public synchronized void recordSuccess() {
        if (state == CircuitState.HALF_OPEN) {
            successCnt.incrementAndGet();
            if (successCnt.get() >= HALF_OPEN_SUCCESS_RATE * reqCnt.get()) {
                state = CircuitState.CLOSED;
                resetCounts();
            }
        } else {
            resetCounts();
        }
    }

    //记录失败
    public synchronized void recordFail() {
        failCnt.incrementAndGet();
        System.out.println(String.format("[CircuitBreaker]失败次数: %s", failCnt));
        lastFailTime = System.currentTimeMillis();
        if (state == CircuitState.HALF_OPEN) {
            state = CircuitState.OPEN;
            lastFailTime = System.currentTimeMillis();
        } else if (failCnt.get() >= FAILURE_THRESHOLD) {
            state = CircuitState.OPEN;
        }
    }

    //重置次数
    private void resetCounts() {
        failCnt.set(0);
        successCnt.set(0);
        reqCnt.set(0);
    }

    public CircuitState getState() {
        return state;
    }
}
