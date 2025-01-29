package com.wisper.client.rpcClient;


import com.wisper.common.message.RpcReqMsg;
import com.wisper.common.message.RpcRespMsg;

public interface RpcClient {
    RpcRespMsg sendMsg(RpcReqMsg reqMsg);
}
