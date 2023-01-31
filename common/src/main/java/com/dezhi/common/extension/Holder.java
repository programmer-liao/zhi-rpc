package com.dezhi.common.extension;

/**
 * 用于 缓存数据, 同时保证可见性
 * @author liaodezhi
 * @date 2023/1/31
 */
public class Holder<T> {

    /**
     * 值
     */
    private volatile T value;

    /**
     * 获取value
     * @return value
     */
    T get() {
        return value;
    }

    /**
     * 设置value
     * @param value value
     */
    public void set(T value) {
        this.value = value;
    }
}
