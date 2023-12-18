package com.dezhi.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
        // 得到当前类路径resources的绝对地址的URL
        URL url = Thread.currentThread().getContextClassLoader().getResource("");;
        String rpcConfigPath = "";
        // 得到fileName的绝对路径
        if (url != null) {
            String path = null;
            try {
                // url路径存在中文时, 中文会转换成url编码, 因此先对路径进行解码
                path = URLDecoder.decode(url.getPath(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("读取 [{}] 时发生异常", fileName);
            }
            // windows下,去除路径前面的"/"
            rpcConfigPath = (path + fileName).substring(1);
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
