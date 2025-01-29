package com.wisper.client.retry;

import com.wisper.client.rpcClient.RpcClient;
import com.wisper.common.message.RpcReqMsg;
import com.wisper.common.message.RpcRespMsg;

public abstract class BaseRetry {
    protected RpcClient rpcClient;

    public BaseRetry(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    //消息重投
    public abstract RpcRespMsg sendMsgRetry(RpcReqMsg reqMsg);

}
