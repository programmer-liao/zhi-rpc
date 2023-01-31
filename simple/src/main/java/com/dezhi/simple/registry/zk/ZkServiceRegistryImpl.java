package com.dezhi.simple.registry.zk;

import com.dezhi.simple.registry.ServiceRegistry;
import com.dezhi.simple.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * zookeeper实现服务注册服务
 *
 * @author liaodezhi
 * @date 2023/1/30
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName    rpc服务名
     * @param inetSocketAddress 服务地址
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // 获取服务路径
        String servicePath = CuratorUtils.ZK_RESISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        // 获取zkClient
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 创建永久节点
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
