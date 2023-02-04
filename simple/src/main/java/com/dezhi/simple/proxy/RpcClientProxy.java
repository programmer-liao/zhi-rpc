package com.dezhi.simple.proxy;

import com.dezhi.common.enums.RpcErrorMessageEnum;
import com.dezhi.common.enums.RpcResponseCodeEnum;
import com.dezhi.common.exception.RpcException;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.remoting.dto.RpcRequest;
import com.dezhi.simple.remoting.dto.RpcResponse;
import com.dezhi.simple.remoting.transport.RpcRequestTransport;
import com.dezhi.simple.remoting.transport.netty.client.NettyRpcClient;
import com.dezhi.simple.remoting.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Rpc动态代理
 * @author liaodezhi
 * @date 2023/2/2
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    /**
     * 接口名称
     */
    private static final String INTERFACE_NAME = "interfaceName";

    /**
     * Rpc请求传输
     */
    private final RpcRequestTransport rpcRequestTransport;

    /**
     * Rpc服务配置
     */
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    /**
     * 获得代理对象
     * @param clazz 被代理类class对象
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }


    /**
     * 动态代理主要方法
     * @param proxy 代理对象
     * @param method 代理方法
     * @param args 方法参数列表
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法: [{}]", method.getName());
        // 构建RpcRequest对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion()).build();
        RpcResponse<Object> rpcResponse = null;
        // 如果是NettyRpcClient
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        // 如果是SocketRpcClient
        if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        // 检查
        this.check(rpcResponse, rpcRequest);
        // 返回数据
        return rpcResponse.getData();
    }


    /**
     * 参数检查
     * @param rpcResponse 响应体
     * @param rpcRequest 请求
     */
    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        // 响应为空
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        // RpcRequest请求Id不等于RpcResponse请求Id
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        // 响应码为空或者响应码为失败情况
        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}