package com.dezhi.simple.remoting.dto;

/**
 * Rpc消息格式
 * @author liaodezhi
 * @date 2023/2/3
 */
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
