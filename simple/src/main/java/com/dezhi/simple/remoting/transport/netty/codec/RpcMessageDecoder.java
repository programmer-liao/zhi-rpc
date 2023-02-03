package com.dezhi.simple.remoting.transport.netty.codec;

import com.dezhi.common.enums.CompressTypeEnum;
import com.dezhi.common.enums.SerializationTypeEnum;
import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.simple.compress.Compress;
import com.dezhi.simple.remoting.constants.RpcConstants;
import com.dezhi.simple.remoting.dto.RpcMessage;
import com.dezhi.simple.remoting.dto.RpcRequest;
import com.dezhi.simple.remoting.dto.RpcResponse;
import com.dezhi.simple.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Rpc消息解码器
 * @author liaodezhi
 * @date 2023/2/3
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    /**
     * 解码操作
     * @param ctx netty上下文对象
     * @param in ByteBuf
     * @return 解码后的对象
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 调用父类解码器
        Object decoded = super.decode(ctx, in);
        // 检查是否为ByteBuf的实例
        if (decoded instanceof ByteBuf) {
            // 转换
            ByteBuf frame = (ByteBuf) decoded;
            // 如果解析后的字节大于协议规定的总长度
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    // 返回解码后的对象
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame错误!", e);
                } finally {
                    // 释放
                    frame.release();
                }
            }
        }
        // 否则返回父类编解码器的结果
        return decoded;
    }

    /**
     * 解析帧
     * @param in ByteBuf
     * @return 解析出的对象
     */
    @SuppressWarnings("all")
    private Object decodeFrame(ByteBuf in) {
        // 必须按顺序读取ByteBuf
        checkMagicNumber(in);
        checkVersion(in);
        // 构建RpcMessage对象
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        // 如果消息类型为心跳请求
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        // 如果消息类型为心跳响应
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        // 计算请求体长度
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        // 如果请求体存在
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            // 读取到bs字节数组中
            in.readBytes(bs);
            // 获取解压缩名称
            String compressName = CompressTypeEnum.getName(compressType);
            // 获取解压缩对象
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            // 解压操作
            bs = compress.decompress(bs);
            // 获取序列化名称
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec的名称: [{}]", codecName);
            // 获取序列化器
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            // 如果消息类型为请求类型
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                // 为响应类型
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        // 返回解码后的消息
        return rpcMessage;
    }
    /**
     * 检查版本
     * @param in ByteBuf
     */
    private void checkVersion(ByteBuf in) {
        // 读取版本
        byte version = in.readByte();
        // 版本不一致
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("版本不一致, 不能运行"+ version);
        }
    }
    private void checkMagicNumber(ByteBuf in) {
        // 获取魔数
        int len = RpcConstants.MAGIC_NUMBER.length;
        // 用于存放魔数的字节数组
        byte[] tmp = new byte[len];
        // 读取魔数到字节数组中
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("无法知晓的魔数" + Arrays.toString(tmp));
            }
        }
    }
}
