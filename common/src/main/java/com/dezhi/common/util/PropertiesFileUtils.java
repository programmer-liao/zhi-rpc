package com.dezhi.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 配置文件读取工具类
 * @author liaodezhi
 * @date 2023/1/29
 */
@Slf4j
public class PropertiesFileUtils {

    /**
     * 读取properties文件内容
     * @param fileName 文件名
     * @return Properties对象
     */
    public static Properties readPropertiesFile(String fileName) {
        // 得到当前类路径的绝对地址的URL
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        // 得到fileName的绝对路径
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        // 将配置文件中的内容加载进Properties集合中
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                Files.newInputStream(Paths.get(rpcConfigPath)),
                StandardCharsets.UTF_8
        )) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("读取 [{}] 时发生异常", fileName);
        }
        return properties;
    }
}
