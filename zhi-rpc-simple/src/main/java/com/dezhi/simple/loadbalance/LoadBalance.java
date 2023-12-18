package com.dezhi.simple.loadbalance;

import com.dezhi.common.extension.SPI;
import com.dezhi.simple.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡接口
 * @author liaodezhi
 * @date 2023/1/31
 */
@SPI
public interface LoadBalance {

    /**
     * 负载均衡 -- 从服务地址列表中选择一个服务地址
     * @param serviceUrlList 服务地址列表
     * @param rpcRequest rpcRequest
     * @return 一个合适的服务地址
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
