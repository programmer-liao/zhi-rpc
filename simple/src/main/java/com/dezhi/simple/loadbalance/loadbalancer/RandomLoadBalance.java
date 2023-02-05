package com.dezhi.simple.loadbalance.loadbalancer;

import com.dezhi.simple.loadbalance.AbstractLoadBalance;
import com.dezhi.simple.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡 -- 随机策略
 * @author liaodezhi
 * @date 2023/2/5
 */
public class RandomLoadBalance extends AbstractLoadBalance {


    /**
     * 随机负载均衡
     * @param serviceAddresses 服务地址列表
     * @param rpcRequest 服务请求体
     * @return 服务地址
     */
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
