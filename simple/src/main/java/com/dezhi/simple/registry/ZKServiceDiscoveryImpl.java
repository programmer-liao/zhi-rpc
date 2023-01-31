package com.dezhi.simple.registry;

import com.dezhi.common.enums.RpcErrorMessageEnum;
import com.dezhi.common.exception.RpcException;
import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.common.util.CollectionUtils;
import com.dezhi.simple.loadbalance.LoadBalance;
import com.dezhi.simple.registry.zk.util.CuratorUtils;
import com.dezhi.simple.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * zookeeper实现服务发现
 * @author liaodezhi
 * @date 2023/1/31
 */
@Slf4j
public class ZKServiceDiscoveryImpl implements ServiceDiscovery {

    /**
     * 负载均衡
     */
    private final LoadBalance loadBalance;

    public ZKServiceDiscoveryImpl() {
        // 通过SPI加载负载均衡器
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    /**
     * 服务发现实现
     * @param rpcRequest 服务请求传输对象
     * @return 服务地址
     */
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        // 获取rpc服务名称
        String rpcServiceName = rpcRequest.getRpcServiceName();
        // 获取zookeeper客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 获取rpcServiceName下的所有子节点
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        // 子节点为空
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡得到服务地址
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("成功发现服务地址: [{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        // 返回
        return new InetSocketAddress(host, port);
    }
}
