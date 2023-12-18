package com.dezhi.common.util;

import java.util.Collection;

/**
 * 集合工具类
 * @author liaodezhi
 * @date 2023/1/28
 */
public class CollectionUtils {

    /**
     * 判断集合是否为空
     * @param c 集合
     * @return 集合为空返回true, 否则返回false
     * @param <T> 集合泛型
     */
    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty();
    }

}
