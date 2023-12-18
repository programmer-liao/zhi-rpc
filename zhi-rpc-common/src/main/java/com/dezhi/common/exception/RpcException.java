package com.dezhi.common.exception;

import com.dezhi.common.enums.RpcErrorMessageEnum;

/**
 * @author liaodezhi
 * @date 2023/1/28
 */
public class RpcException extends RuntimeException {

    /**
     * @param rpcErrorMessageEnum RpcException message 枚举对象
     * @param detail 详细描述信息
     */
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    /**
     * @param message 异常信息
     * @param cause Throwable对象
     */
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param rpcErrorMessageEnum RpcException message 枚举对象
     */
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
