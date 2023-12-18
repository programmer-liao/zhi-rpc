package com.dezhi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Rpc配置枚举类
 * @author liaodezhi
 * @date 2023/1/28
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     * Rpc配置文件路径
     */
    RPC_CONFIG_PATH("rpc.properties"),

    /**
     * ZooKeeper地址
     */
    ZK_ADDRESS("rpc.zookeeper.address");
    /**
     * 配置属性值
     */
    private final String propertyValue;
}
