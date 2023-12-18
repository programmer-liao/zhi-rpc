package com.dezhi.common.extension;

import com.dezhi.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 扩展加载器
 *
 * @author liaodezhi
 * @date 2023/1/31
 */
@SuppressWarnings("all")
@Slf4j
public class ExtensionLoader<T> {

    /**
     * 扩展目录
     */
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    /**
     * 用于存放通过扩展加载程序加载的类 Map
     * key: Class<?> 类对象
     * value: ExtensionLoader<?> 扩展加载器
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * 用于存放通过扩展加载程序加载的实例 Map
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();
    /**
     * Class类型
     */
    private final Class<?> type;

    /**
     * 缓存实例的Map
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 用户缓存类的Holder
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    /**
     * @param type Class类型
     */
    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * 获取扩展加载器
     *
     * @param type Class对象
     * @return 扩展加载器
     */
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        // type为null
        if (type == null) {
            throw new IllegalArgumentException("扩展类型不应该为空");
        }
        // type不是接口类型
        if (!type.isInterface()) {
            throw new IllegalArgumentException("扩展类型应该是一个接口");
        }
        // 没有被@SPI注解
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("扩展类型必须被@SPI注解");
        }
        // 首先从缓存中取, 如果没有命中, 就创建一个
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    /**
     * 获取扩展类型实例
     *
     * @param name 扩展类型名称
     * @return 扩展类型实例
     */
    public T getExtension(String name) {
        // 扩展名称为空
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("扩展名称不能为空");
        }
        // 首先从缓存中取, 如果没有命中, 就创建一个
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instacne = holder.get();
        // 创建一个单例如果实例不存在
        if (instacne == null) {
            synchronized (holder) {
                instacne = holder.get();
                if (instacne == null) {
                    synchronized (holder) {
                        instacne = holder.get();
                        if (instacne == null) {
                            instacne = createExtension(name);
                            holder.set(instacne);
                        }
                    }
                }
            }
        }
        return (T) instacne;
    }

    /**
     * 创建扩展类对象
     *
     * @param name 扩展类对象名称
     * @return 扩展类对象
     */
    private T createExtension(String name) {
        // 获取Class对象
        Class<?> clazz = getExtensionClasses().get(name);
        // 对象为空
        if (clazz == null) {
            throw new RuntimeException("没有这样的扩展名称: " + name);
        }
        // 获取实例
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        // 实例不存在, 创建
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        // 返回实例
        return instance;
    }

    /**
     * 获取 ExtensionClasses
     *
     * @return ExtensionClasses
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // 首先从缓存中取, 缓存中没有再去创建
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                // 双重检查
                classes = cachedClasses.get();
                // 没有就去创建
                if (classes == null) {
                    synchronized (cachedClasses) {
                        classes = cachedClasses.get();
                        if (classes == null) {
                            classes = new HashMap<>();
                            // 加载并放入HashMap中
                            loadDirectory(classes);
                            cachedClasses.set(classes);
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * 从文件中加载类对象
     *
     * @param extensionClasses 存放类对象的Map
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        // 获得文件名
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    // 加载类对象
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 从ResourceUrl中读取全类名并且加载类
     *
     * @param extensionClasses 扩展类对象Map
     * @param classLoader      类加载器
     * @param resourceUrl      资源Url
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        // 从resourceUrl中加载数据
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), Charset.defaultCharset()))) {
            // 读取数据
            String line;
            // 读取0 - 最后一个'#'之前的所有字符并存入line中
            while ((line = reader.readLine()) != null) {
                final int ci = line.indexOf('#');

                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                // 去除空格
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        // 提取clazzName
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        if (name.length() > 0 && clazzName.length() > 0) {
                            // 加载类
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            // 并且存入extensionClasses中
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
