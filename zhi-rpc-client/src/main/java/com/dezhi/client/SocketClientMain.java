package com.dezhi.client;

import com.dezhi.api.Hello;
import com.dezhi.api.HelloService;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.proxy.RpcClientProxy;
import com.dezhi.simple.remoting.transport.RpcRequestTransport;
import com.dezhi.simple.remoting.transport.socket.SocketRpcClient;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
public class SocketClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
