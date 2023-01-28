package com.dezhi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化方案枚举类
 * @author liaodezhi
 * @date 2023/1/28
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0x03, "hessian");

    /**
     * 序列化方式识别码
     */
    private final byte code;

    /**
     * 序列化方式名称
     */
    private final String name;

    /**
     * 通过识别码code获取对应序列化方式名称
     * @param code 识别码
     * @return 序列化方式名称
     */
    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
