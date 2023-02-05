package com.dezhi.simple.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.dezhi.simple.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian实现序列化
 *
 * @author liaodezhi
 * @date 2023/2/5
 */
public class HessianSerializer implements Serializer {

    /**
     * 序列化
     *
     * @param obj 序列化对象
     * @return 序列化后的字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("序列化失败");
        }
    }

    /**
     * 反序列化
     *
     * @param bytes 原始序列化数组
     * @param clazz 反序列化后应该得到的类型
     * @return 返回反序列化后的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            Object o = hessianInput.readObject();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
