package com.wisper.common.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcRespMsg extends Message {
    private Integer code;
    private String msg;
    private Object data;
    public static RpcRespMsg success(Object data) {
        return RpcRespMsg.builder().code(200).data(data).build();
    }

    public static RpcRespMsg fail(Integer code, String msg){
        return RpcRespMsg.builder().code(code).msg(msg).build();
    }

    public static RpcRespMsg fail(){
        return RpcRespMsg.builder().code(500).msg("远程调用异常").build();
    }

    @Override
    public int getMessageType() {
        return RPC_RESPONSE_MESSAGE;
    }
}
