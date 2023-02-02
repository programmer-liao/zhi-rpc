package com.dezhi.simple.remoting.dto;

import com.dezhi.common.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * rpc响应体
 *
 * @author liaodezhi
 * @date 2023/2/2
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcResponse<T> implements Serializable {

    /**
     * 序列化版本
     */
    private static final long serialVersionUID = 0;

    /**
     * 请求Id
     */
    private String requestId;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应成功
     * @param data 响应数据
     * @param requestId 请求Id
     * @return 响应封装
     */
    public static <T> RpcResponse<T> success(T data , String requestId) {
        // 响应体封装
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (data != null) {
            response.setData(data);
        }
        // 返回
        return response;
    }

    /**
     * 失败响应
     * @param rpcResponseCodeEnum 失败情况枚举
     * @return 响应封装
     */
    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        // 响应体封装
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        // 返回
        return response;
    }
}
