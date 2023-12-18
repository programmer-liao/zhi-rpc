package com.dezhi.common.util.concurrent.threadpool;

import lombok.Getter;
import lombok.Setter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池参数配置
 * @author liaodezhi
 * @date 2023/1/28
 */
@Getter
@Setter
public class CustomThreadPoolConfig {

    /* ---- 线程池默认参数 ---- */

    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;

    /**
     * 默认最大线程数
     */
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;

    /**
     * 默认空闲线程存活时间
     */
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;

    /**
     * 默认时间单位
     */
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    /**
     * 默认阻塞队列容量
     */
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;

    /* ---- 可配置参数 ---- */

    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;
    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;

    /**
     * 工作队列
     * Thread实现了Runnable接口, 所以泛型指定为Runnable
     */
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(DEFAULT_BLOCKING_QUEUE_CAPACITY);

}
