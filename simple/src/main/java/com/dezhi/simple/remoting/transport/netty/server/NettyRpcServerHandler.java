package com.dezhi.simple.remoting.transport.netty.server;

import com.dezhi.common.enums.CompressTypeEnum;
import com.dezhi.common.enums.RpcResponseCodeEnum;
import com.dezhi.common.enums.SerializationTypeEnum;
import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.simple.remoting.constants.RpcConstants;
import com.dezhi.simple.remoting.dto.RpcMessage;
import com.dezhi.simple.remoting.dto.RpcRequest;
import com.dezhi.simple.remoting.dto.RpcResponse;
import com.dezhi.simple.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
/**
 * NettyRpcServerHandler
 *
 * @author liaodezhi
 * @date 2023/2/4
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * RpcRequestHandler
     */
    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    /**
     * 读事件
     * @param ctx 上下文对象
     * @param msg 消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                log.info("服务器收到消息: [{}]", msg);
                // 消息类型
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.HESSIAN.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                // 如果是心跳类型
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    // 否则
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("服务器收到结果: %s", result.toString()));
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    // 如果channel处于运行状态并且可写
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        // 成功
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        // 失败
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("消息丢失");
                    }
                }
                // 向通道中写入消息
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            // 释放
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果是IdleStateEvent的实例
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("空闲检查开启, 关闭连接");
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器出现异常");
        cause.printStackTrace();
        ctx.close();
    }
}
