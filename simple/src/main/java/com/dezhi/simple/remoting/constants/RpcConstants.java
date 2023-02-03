package com.dezhi.simple.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Rpc常量
 *
 * @author liaodezhi
 * @date 2023/2/3
 */
public class RpcConstants {

    /**
     * 魔数
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'z', (byte) 'r', (byte) 'p', (byte) 'c'};

    /**
     * 默认字符集
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 版本
     */
    public static final byte VERSION = 1;
    /**
     * 总长度
     */
    public static final byte TOTAL_LENGTH = 16;

    /**
     * 请求类型
     */
    public static final byte REQUEST_TYPE = 1;

    /**
     * 响应类型
     */
    public static final byte RESPONSE_TYPE = 2;

    /**
     * 心跳请求类型
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;

    /**
     * 心跳响应类型
     */
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    /**
     * 请求头大小
     */
    public static final int HEAD_LENGTH = 16;

    public static final String PING = "ping";

    public static final String PONG = "pong";

    /**
     * 最大帧长度
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
