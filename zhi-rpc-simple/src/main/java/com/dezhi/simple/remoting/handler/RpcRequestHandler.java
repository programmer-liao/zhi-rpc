package com.dezhi.simple.remoting.handler;

import com.dezhi.common.exception.RpcException;
import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.simple.provider.ServiceProvider;
import com.dezhi.simple.provider.impl.ZkServiceProviderImpl;
import com.dezhi.simple.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RpcRequest 处理器
 * @author liaodezhi
 * @date 2023/2/4
 */
@Slf4j
public class RpcRequestHandler {

    /**
     * 服务提供者
     */
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 处理
     * @param rpcRequest rpc请求
     * @return 结果
     */
    public Object handle(RpcRequest rpcRequest) {
        // 服务
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        // 返回调用结果
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 调用目标方法
     * @param service 服务
     * @return 返回调用结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        // 调用结果
        Object result;
        try {
            // 方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info(("服务调用成功, [{}], [{}]"),rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(), e);
        }
        // 返回结果
        return result;
    }
}
