package com.dezhi.simple.serialize.kyro;

import com.dezhi.common.exception.SerializeException;
import com.dezhi.simple.remoting.dto.RpcRequest;
import com.dezhi.simple.remoting.dto.RpcResponse;
import com.dezhi.simple.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo实现序列化
 *
 * @author liaodezhi
 * @date 2023/2/5
 */
@Slf4j
public class KryoSerializer implements Serializer {

    /**
     * ThreadLocal保存Kryo
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    /**
     * 序列化
     *
     * @param obj 序列化对象
     * @return 序列化后的字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            // 获取kryo对象
            Kryo kryo = kryoThreadLocal.get();
            // 序列化
            kryo.writeObject(output, obj);
            // 记得移除
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException("序列化失败");
        }
    }

    /**
     * 反序列化
     * @param bytes 原始序列化数组
     * @param clazz 反序列化后应该得到的类型
     * @return 返回反序列化后的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        } catch (IOException e) {
          throw new SerializeException("反序列化失败");
        }
    }
}
