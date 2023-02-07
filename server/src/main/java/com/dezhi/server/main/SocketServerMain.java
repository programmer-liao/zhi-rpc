package com.dezhi.server.main;

import com.dezhi.server.serviceimpl.HelloServiceImpl;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.remoting.transport.socket.SocketRpcServer;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
public class SocketServerMain {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(helloService);
        socketRpcServer.registerService(rpcServiceConfig);
        socketRpcServer.start();
    }
}
