package com.wisper.server.ratelimit.limiter;

public interface RateLimiter {
    /**
     * 获取令牌
     * @return boolean
     */
    boolean getToken();
}
