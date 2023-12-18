package com.dezhi.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 字符串工具类测试
 * @author liaodezhi
 * @date 2023/1/29
 */
public class StringUtilsTest {

    @Test
    void test_is_black() {
        String s = null;
        Assertions.assertTrue(StringUtils.isBlank(s));
        s = "         ";
        Assertions.assertTrue(StringUtils.isBlank(s));
        s = "";
        Assertions.assertTrue(StringUtils.isBlank(s));
        s = "ok";
        Assertions.assertFalse(StringUtils.isBlank(s));
    }
}
