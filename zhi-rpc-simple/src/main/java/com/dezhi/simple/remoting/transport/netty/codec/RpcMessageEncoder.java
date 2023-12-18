package com.dezhi.simple.remoting.transport.netty.codec;

import com.dezhi.common.enums.CompressTypeEnum;
import com.dezhi.common.enums.SerializationTypeEnum;
import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.simple.compress.Compress;
import com.dezhi.simple.remoting.constants.RpcConstants;
import com.dezhi.simple.remoting.dto.RpcMessage;
import com.dezhi.simple.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rpc消息编码器
 * @author liaodezhi
 * @date 2023/2/3
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    /**
     * 原子类ATOMIC_INTEGER, 用于RequestId
     */
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        // 魔数
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        // 版本
        out.writeByte(RpcConstants.VERSION);
        // 预先保留4byte用于填写 full length
        out.writerIndex(out.writerIndex() + 4);
        // 消息类型
        byte messageType = rpcMessage.getMessageType();
        out.writeByte(messageType);
        // 消息序列化类型
        out.writeByte(rpcMessage.getCodec());
        // 解压缩类型
        out.writeByte(CompressTypeEnum.GZIP.getCode());
        // 版本号
        out.writeInt(ATOMIC_INTEGER.getAndIncrement());
        // 获取full length
        byte[] bodyBytes = null;
        int fullLength = RpcConstants.HEAD_LENGTH;
        // 如果消息类型不是心跳信息, fullLength = head length + body length
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType!= RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            // 序列化名称
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: [{}]", codecName);
            // 获取序列化器
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            // 序列化
            bodyBytes = serializer.serialize(rpcMessage.getData());
            // 获取解压缩器
            String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            // 压缩
            bodyBytes = compress.compress(bodyBytes);
            // 计算 full length
            fullLength += bodyBytes.length;
        }
        // 如果请求体不为空
        if (bodyBytes != null) {
            out.writeBytes(bodyBytes);
        }
        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
        out.writeInt(fullLength);
        out.writerIndex(writeIndex);
    }
}
