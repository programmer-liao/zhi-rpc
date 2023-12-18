package com.dezhi.simple.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rpc消息格式
 * @author liaodezhi
 * @date 2023/2/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMessage {

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 序列化类型
     */
    private byte codec;

    /**
     * 解压缩类型
     */
    private byte compress;

    /**
     * 请求Id
     */
    private int requestId;

    /**
     * 数据
     */
    private Object data;
}
