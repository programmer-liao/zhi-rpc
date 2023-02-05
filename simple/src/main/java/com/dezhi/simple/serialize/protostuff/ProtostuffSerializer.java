package com.dezhi.simple.serialize.protostuff;

import com.dezhi.simple.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;


/**
 * ProtostuffSerializer实现序列化
 * @author liaodezhi
 * @date 2023/2/5
 */
public class ProtostuffSerializer implements Serializer {

    /**
     * 缓冲buffer
     */
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    /**
     * 序列化
     * @param obj 序列化对象
     * @return 返回序列化后的字节数组
     */
    @Override
    @SuppressWarnings("all")
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    /**
     * 反序列化
     * @param bytes 原始序列化数组
     * @param clazz 反序列化后应该得到的类型
     * @return 反序列化得到的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
