package com.dezhi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RpcException message 枚举类
 * @author liaodezhi
 * @date 2023/1/28
 */
@AllArgsConstructor
@Getter
public enum RpcErrorMessageEnum {

    Client_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_FOUND("没有找到指定的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务没有实现任何接口"),
    REQUEST_NOT_MATCH_RESPONSE("返回结果错误！请求和返回的响应不匹配");

    /**
     * 错误提示
     */
    private final String message;
}
