package com.dezhi.simple.loadbalance;

import com.dezhi.common.util.CollectionUtils;
import com.dezhi.simple.remoting.dto.RpcRequest;
import java.util.List;

/**
 * 负载均衡抽象类
 * @author liaodezhi
 * @date 2023/1/31
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        // 服务地址为空,返回null
        if (CollectionUtils.isEmpty(serviceAddresses)) {
            return null;
        }
        // 只有一个服务地址的情况
        if (serviceAddresses.size() == 1) {
            // 就决定是你了
            return serviceAddresses.get(0);
        }
        // 返回负载均衡后的结果
        return doSelect(serviceAddresses, rpcRequest);
    }

    /**
     * 交由实现类实现具体的负载均衡算法
     * @param serviceAddresses 服务地址列表
     * @param rpcRequest 服务请求体
     * @return 选择后的服务地址
     */
    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}
