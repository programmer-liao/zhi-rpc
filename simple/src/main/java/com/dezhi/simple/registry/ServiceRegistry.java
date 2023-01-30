package com.dezhi.simple.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 * @author liaodezhi
 * @date 2023/1/30
 */
public interface ServiceRegistry {

    /**
     * 服务注册
     * @param rpcServiceName rpc服务名
     * @param inetSocketAddress 服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
