package com.dezhi.simple.registry;

import com.dezhi.simple.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现服务
 * @author liaodezhi
 * @date 2023/1/30
 */
public interface ServiceDiscovery {
    /**
     * 通过rpcRequest发现服务地址
     * @param rpcRequest 服务请求传输对象
     * @return 服务地址
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
