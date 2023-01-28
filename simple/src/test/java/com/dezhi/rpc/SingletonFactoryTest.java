package com.dezhi.rpc;

import com.dezhi.common.factory.SingletonFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 抽象工厂方法类测试
 * @author liaodezhi
 * @date 2023/1/28
 */
public class SingletonFactoryTest {

    /**
     * 测试SingletonFactory两次创建的对象是否是同一个
     */
    @Test
    void test_singleton_factory() {
        Object instance1 = SingletonFactory.getInstance(Object.class);
        Object instance2 = SingletonFactory.getInstance(Object.class);
        Assertions.assertEquals(instance1, instance2);
    }
}
