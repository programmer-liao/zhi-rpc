package com.dezhi.common.factory;

import com.dezhi.common.util.concurrent.threadpool.CustomThreadPoolConfig;
import com.dezhi.common.util.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池工程工具类测试
 * @author liaodezhi
 * @date 2023/1/29
 */
@Slf4j
public class TheadPoolFactoryUtilsTest {
    static ExecutorService threadPool1;
    static ExecutorService threadPool2;
    static ExecutorService threadPool3;

    /**
     * 测试线程池创建
     */
    @Test
    @Before
    public void test_create_thread_pool() {
        threadPool1 = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("customThreadPool1");
        threadPool2 = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("customThreadPool2", new CustomThreadPoolConfig());
        threadPool3 = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent(new CustomThreadPoolConfig(), "customThreadPool3", true);
        Assertions.assertNotNull(threadPool1);
        Assertions.assertNotNull(threadPool2);
        Assertions.assertNotNull(threadPool3);
    }

    /**
     * 测试打印线程池状态
     */
    @Test
    void test_print_thread_pool_status() {
        ThreadPoolFactoryUtils.printThreadPoolStatus((ThreadPoolExecutor) threadPool1);
        ThreadPoolFactoryUtils.printThreadPoolStatus((ThreadPoolExecutor) threadPool2);
        ThreadPoolFactoryUtils.printThreadPoolStatus((ThreadPoolExecutor) threadPool3);
    }

    /**
     * 测试创建线程工厂
     */
    @Test
    void test_create_thread_pool_factory() {
        ThreadFactory threadFactory = ThreadPoolFactoryUtils.createThreadFactory("customThreadFactory", true);
        Assertions.assertNotNull(threadFactory);
    }
}
