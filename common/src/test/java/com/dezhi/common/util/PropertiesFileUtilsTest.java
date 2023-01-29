package com.dezhi.common.util;

import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * 配置文件读取工具类
 * @author liaodezhi
 * @date 2023/1/29
 */
public class PropertiesFileUtilsTest {

    @Test
    void test_properties_file_read() {
        Properties properties = PropertiesFileUtils.readPropertiesFile("test.properties");
        System.out.println(properties.getProperty("name"));
        System.out.println(properties.getProperty("message"));
    }
}
