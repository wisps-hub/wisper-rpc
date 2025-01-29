package com.wisper.common.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();
    public static final int RPC_REQUEST_MESSAGE = 0;
    public static final int  RPC_RESPONSE_MESSAGE = 1;

    static {
        messageClasses.put(RPC_REQUEST_MESSAGE, RpcReqMsg.class);
        messageClasses.put(RPC_RESPONSE_MESSAGE, RpcRespMsg.class);
    }
}
