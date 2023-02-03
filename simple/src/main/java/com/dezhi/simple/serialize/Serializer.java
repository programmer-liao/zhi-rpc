package com.dezhi.simple.serialize;

import com.dezhi.common.extension.SPI;

/**
 * 序列化接口
 * @author liaodezhi
 * @date 2023/2/3
 */
@SPI
public interface Serializer {


    /**
     * 序列化
     * @param obj 序列化对象
     * @return 序列化字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 原始序列化数组
     * @param clazz 反序列化后应该得到的类型
     * @return 序列化后的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
