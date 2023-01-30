package com.dezhi.simple.registry.zk;

import com.dezhi.simple.registry.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * zookeeper实现服务注册服务
 * @author liaodezhi
 * @date 2023/1/30
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = Cu
    }

}
