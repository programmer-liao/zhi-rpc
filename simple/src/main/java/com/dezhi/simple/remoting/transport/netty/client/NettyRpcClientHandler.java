package com.dezhi.simple.remoting.transport.netty.client;

import com.dezhi.common.enums.CompressTypeEnum;
import com.dezhi.common.enums.SerializationTypeEnum;
import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.simple.remoting.constants.RpcConstants;
import com.dezhi.simple.remoting.dto.RpcMessage;
import com.dezhi.simple.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author liaodezhi
 * @date 2023/2/3
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * UnprocessedRequests
     */
    private final UnprocessedRequests unprocessedRequests;

    /**
     * NettyRpcClient
     */
    private final NettyRpcClient nettyRpcClient;

    /**
     * 构造方法
     */
    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.nettyRpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    /**
     * 读事件
     * @param ctx 上下文对象
     * @param msg 消息
     */
    @Override
    @SuppressWarnings("all")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("客户端收到信息: [{}]", msg);
            // 如果是RpcMessage的实例
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", tmp.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            // 释放
            ReferenceCountUtil.release(msg);
        }
    }
    /**
     * 触发心跳机制时调用
     * @param ctx 上下文对象
     * @param evt IdleStateEvent对象
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果为IdleStateEvent实例
        if (evt instanceof IdleStateEvent) {
            // 获取状态
            IdleState state = ((IdleStateEvent) evt).state();
            // 如果状态为WRITER_IDLE
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                // 获取channel对象
                Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                // 构建RpcMessage对象
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData(RpcConstants.PING);
                // 写入Channel
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                // 其他事件调用父类
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    /**
     * 异常发生时调用
     * @param ctx 上下文对象
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端发生异常: ", cause);
        cause.printStackTrace();
        // 关闭ctx
        ctx.close();
    }
}
