package com.wisper.client.retry;

import com.github.rholder.retry.*;
import com.wisper.client.rpcClient.RpcClient;
import com.wisper.common.message.RpcReqMsg;
import com.wisper.common.message.RpcRespMsg;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GuavaRetry extends BaseRetry{

    public GuavaRetry(RpcClient rpcClient) {
        super(rpcClient);
    }

    @Override
    public RpcRespMsg sendMsgRetry(RpcReqMsg reqMsg) {
        Retryer<RpcRespMsg> retryer = RetryerBuilder.<RpcRespMsg>newBuilder()
                //无论出现什么异常，都进行重试
                .retryIfException()
                //返回结果为 error时进行重试
                .retryIfResult(respMsg -> Objects.equals(respMsg.getCode(), 500))
                //重试等待策略：等待 2s 后再进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //重试停止策略：重试达到 3 次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println(String.format("[GuavaRetry]: 第%s次调用", attempt.getAttemptNumber()));
                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendMsg(reqMsg));
        } catch (Exception e) {
            System.out.println("[GuavaRetry]请求失败: " + e);
        }
        return RpcRespMsg.fail();
    }

}
