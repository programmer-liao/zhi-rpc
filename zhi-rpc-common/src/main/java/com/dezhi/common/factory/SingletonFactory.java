package com.dezhi.common.factory;


import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例对象工厂类
 * @author liaodezhi
 * @date 2023/1/28
 */
public final class SingletonFactory {
    /**
     * 对象Map, 用于收集所有通过单例对象工厂生成的对象
     */
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    /**
     * 获得单例对象
     * @param c 单例类的class对象
     * @return 单例对象
     * @param <T> 单例类
     * @throws IllegalArgumentException 非法参数异常
     */
    public static <T> T getInstance(Class<T> c) throws RuntimeException {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        String key = c.toString();
        // 已经存在相应的对象, 直接返回存在的对象
        if (OBJECT_MAP.containsKey(key)) {
            return c.cast(OBJECT_MAP.get(key));
        }
        // 没有则返回创建的对象
        return c.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
