package com.dezhi.simple.provider.impl;

import com.dezhi.common.enums.RpcErrorMessageEnum;
import com.dezhi.common.exception.RpcException;
import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.provider.ServiceProvider;
import com.dezhi.simple.registry.ServiceRegistry;
import com.dezhi.simple.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zookeeper 实现服务提供者
 * @author liaodezhi
 * @date 2023/2/1
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    /**
     * serviceMap
     * key: rpc服务名称(interface name + version + group)
     * value: 服务对象
     */
    private final Map<String, Object> serviceMap;

    /**
     * 用于存放已经注册的服务
     */
    private final Set<String> registeredService;

    /**
     * 服务注册服务
     */
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        // 从扩展加载器中获取
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }


    /**
     * 添加服务
     * @param rpcServiceConfig 服务配置信息
     */
    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        // 获得服务名称
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        // 服务已经添加, 返回
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        // 添加服务
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("添加服务: {} 和接口: {}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    /**
     * 获取服务对象
     * @param rpcServiceName rpc服务名称
     * @return 服务对象
     */
    @Override
    public Object getService(String rpcServiceName) {
        // 取出服务
        Object service = serviceMap.get(rpcServiceName);
        // 服务没有找到
        if (service == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    /**
     * 发布对应的rpc服务配置信息
     * @param rpcServiceConfig rpc服务配置信息
     */
    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("获取HostAddress时发生异常", e);
        }
    }
}
