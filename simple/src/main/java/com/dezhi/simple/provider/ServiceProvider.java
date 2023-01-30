package com.dezhi.simple.provider;

import com.dezhi.simple.config.RpcServiceConfig;

/**
 * 存储和提供服务对象
 * @author liaodezhi
 * @date 2023/1/30
 */
public interface ServiceProvider {

    /**
     * 添加服务
     * @param rpcServiceConfig 服务配置信息
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获取服务对象
     * @param rpcServiceName rpc服务名称
     * @return 服务对象
     */
    Object getService(String rpcServiceName);

    /**
     * 打印对应的rpc服务配置信息
     * @param rpcServiceConfig rpc服务配置信息
     */
    void publishService(RpcServiceConfig rpcServiceConfig);
}
