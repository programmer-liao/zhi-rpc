package com.dezhi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Rpc响应状态码枚举类
 * @author liaodezhi
 * @date 2023/1/28
 */
@AllArgsConstructor
@Getter
public enum RpcResponseCodeEnum {

    SUCCESS(200, "远程调用成功"),
    FAIL(500, "远程调用失败");
    /**
     * 响应状态码
     */
    private final int code;

    /**
     * 响应状态码描述信息
     */
    private final String message;
}
