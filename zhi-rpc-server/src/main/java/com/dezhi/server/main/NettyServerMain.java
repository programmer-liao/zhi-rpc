package com.dezhi.server.main;

import com.dezhi.server.serviceimpl.HelloServiceImpl2;
import com.dezhi.simple.annotation.RpcScan;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
@RpcScan(basePackage = {"com.dezhi"})
public class NettyServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        HelloServiceImpl2 helloService2 = new HelloServiceImpl2();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2")
                .version("version2")
                .service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
