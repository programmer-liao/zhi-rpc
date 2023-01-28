package com.dezhi.common.util;

import org.junit.jupiter.api.Test;

/**
 * Runtime工具类测试
 *
 * @author liaodezhi
 * @date 2023/1/28
 */
public class RuntimeUtilsTest {

    /**
     * 测试获取CPU核心数方法
     */
    @Test
    void test_get_cpus() {
        System.out.println(RuntimeUtils.cpus());
    }
}
