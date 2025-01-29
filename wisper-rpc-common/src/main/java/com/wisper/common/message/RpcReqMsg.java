package com.wisper.common.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcReqMsg extends Message {
    /**
     * 调用接口名
     */
    private String interfaceName;
    /**
     * 调用接口中的方法名
     */
    private String methodName;
    /**
     * 方法参数类型数组
     */
    private Class<?>[] paramTypes;
    /**
     * 方法参数值数组
     */
    private Object[] params;

    @Override
    public int getMessageType() {
        return RPC_REQUEST_MESSAGE;
    }
}
