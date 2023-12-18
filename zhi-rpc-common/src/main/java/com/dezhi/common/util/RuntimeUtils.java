package com.dezhi.common.util;

/**
 * Runtime工具类
 * @author liaodezhi
 * @date 2023/1/28
 */
public class RuntimeUtils {

    /**
     * 获取CPU的核心数
     * @return CPU的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
