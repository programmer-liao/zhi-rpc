package com.dezhi.simple.remoting.transport.netty.client;

import com.dezhi.common.enums.CompressTypeEnum;
import com.dezhi.common.enums.SerializationTypeEnum;
import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.simple.registry.ServiceDiscovery;
import com.dezhi.simple.remoting.constants.RpcConstants;
import com.dezhi.simple.remoting.dto.RpcMessage;
import com.dezhi.simple.remoting.dto.RpcRequest;
import com.dezhi.simple.remoting.dto.RpcResponse;
import com.dezhi.simple.remoting.transport.RpcRequestTransport;
import com.dezhi.simple.remoting.transport.netty.codec.RpcMessageDecoder;
import com.dezhi.simple.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Netty实现RpcClient
 *
 * @author liaodezhi
 * @date 2023/2/2
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    /**
     * 服务发现
     */
    private final ServiceDiscovery serviceDiscovery;


    /**
     * 没有处理的请求
     */
    private final UnprocessedRequests unprocessedRequests;

    /**
     * Channel提供者
     */
    private final ChannelProvider channelProvider;

    /**
     * Netty Bootstrap
     */
    private final Bootstrap bootstrap;

    /**
     * EventLoopGroup
     */
    private final EventLoopGroup eventLoopGroup;

    /**
     * 构造函数
     */
    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup) // 设置EventLoopGroup
                .channel(NioSocketChannel.class) // 设置Channel
                .handler(new LoggingHandler(LogLevel.INFO)) // 设置日志输出
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)// 设置感兴趣的事件
                .handler(new ChannelInitializer<SocketChannel>() { // 设置处理器
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 得到pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS)) // 心跳机制
                                .addLast(new RpcMessageEncoder()) // 编码器
                                .addLast(new RpcMessageDecoder()) // 解码器
                                .addLast(new NettyRpcClientHandler()); // 请求处理器
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // CompletableFuture
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // inetSocketAddress
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        // 获取相关的channel
        Channel channel = getChannel(inetSocketAddress);
        // 如果channel处于active状态
        if (channel.isActive()) {
            // 放入未处理的请求中
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            // 构建RpcMessage
            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            // 写入Channel中
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端发送消息: [{}]", rpcMessage);
                } else {
                    // 失败就关闭channel
                    future.channel().close();
                    // 出现异常
                    resultFuture.completeExceptionally(future.cause());
                    log.error("发送失败: ", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        // 返回
        return resultFuture;
    }

    /**
     * 连接动作
     *
     * @param inetSocketAddress 地址
     * @return Channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        // 异步调用
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        // 添加监听
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            // 成功
            if (future.isSuccess()) {
                log.info("客户端连接成功:[{}]", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                // 失败
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    /**
     * 获取Channel
     *
     * @param inetSocketAddress 地址
     * @return Channel
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        // channel对象
        Channel channel = channelProvider.get(inetSocketAddress);
        // channel为空
        if (channel == null) {
            // 连接
            channel = doConnect(inetSocketAddress);
            // 保存到ChannelProvider中
            channelProvider.set(inetSocketAddress, channel);
        }
        // 返回channel
        return channel;
    }

    /**
     * 关闭
     */
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
