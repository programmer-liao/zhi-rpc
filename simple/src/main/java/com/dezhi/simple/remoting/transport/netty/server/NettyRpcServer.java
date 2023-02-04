package com.dezhi.simple.remoting.transport.netty.server;

import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.common.util.RuntimeUtils;
import com.dezhi.common.util.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.dezhi.simple.config.CustomShutdownHook;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.provider.ServiceProvider;
import com.dezhi.simple.provider.impl.ZkServiceProviderImpl;
import com.dezhi.simple.remoting.transport.netty.codec.RpcMessageDecoder;
import com.dezhi.simple.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author liaodezhi
 * @date 2023/2/1
 */
@Component
@Slf4j
public class NettyRpcServer {

    /**
     * 端口
     */
    public static final int PORT = 9998;

    /**
     * 服务提供者
     */
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);

    /**
     * 注册服务
     *
     * @param rpcServiceConfig 服务配置
     */
    public void registerService(RpcServiceConfig rpcServiceConfig) {
        // 发布服务
        serviceProvider.publishService(rpcServiceConfig);
    }

    /**
     * 启动服务器
     */
    @SneakyThrows
    public void start() {
        // 清除
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        // BossGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // WorkerGroup
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        // serviceHandlerGroup
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtils.cpus() * 2,
                ThreadPoolFactoryUtils.createThreadFactory("service-handler-group", false)
        );
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new RpcMessageEncoder())
                                    .addLast(new RpcMessageDecoder())
                                    .addLast(serviceHandlerGroup, new NettyRpcServerHandler());

                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(host, PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            log.error("关闭服务端");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
