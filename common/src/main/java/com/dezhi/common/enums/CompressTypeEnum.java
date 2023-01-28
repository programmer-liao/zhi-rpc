package com.dezhi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩类型枚举类
 * @author liaodezhi
 * @date 2023/1/28
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");
    /**
     * 压缩类型代码
     */
    private final byte code;
    /**
     * 压缩类型名称
     */
    private final String name;

    /**
     * 根据压缩类型代码获取压缩类型名称
     * @param code 压缩类型代码
     * @return 压缩类型名称
     */
    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
