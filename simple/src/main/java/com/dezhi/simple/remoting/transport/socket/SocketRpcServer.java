package com.dezhi.simple.remoting.transport.socket;

import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.common.util.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.dezhi.simple.config.CustomShutdownHook;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.provider.ServiceProvider;
import com.dezhi.simple.provider.impl.ZkServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;

import static com.dezhi.simple.remoting.transport.netty.server.NettyRpcServer.PORT;

/**
 * SocketRpcServer
 * @author liaodezhi
 * @date 2023/2/6
 */
@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("客户端已连接: [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("出现异常", e);
        }
    }
}
