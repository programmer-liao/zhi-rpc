package com.dezhi.common.util.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工程工具类
 * @author liaodezhi
 * @date 2023/1/29
 */
@Slf4j
public class ThreadPoolFactoryUtils {

    /**
     * 可以通过 threadNamePrefix 区分不同的线程池, 因为 threadNamePrefix 代表一组业务
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();


    /**
     * 创建自定义线程池如果线程池不存在
     * 1. 使用默认的线程池配置
     * 2. 线程池对象创建的线程为普通线程
     * @param threadNamePrefix 线程池创建的线程名称前缀
     * @return 线程池对象
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    /**
     * 创建自定义线程池如果线程池不存在
     * 1. 线程池对象创建的线程为普通线程
     * @param threadNamePrefix 线程池创建的线程名称前缀
     * @param customThreadPoolConfig 线程池配置
     * @return 线程池对象
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, CustomThreadPoolConfig customThreadPoolConfig) {
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }
    /**
     * 创建自定义线程池如果线程池不存在
     * @param customThreadPoolConfig 线程池配置
     * @param threadNamePrefix 线程池创建的线程名称前缀
     * @param daemon 是否指定为守护线程
     * @return 线程池对象
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        // 如果线程池不存在, 就创建一个
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        // 如果线程池被关闭, 就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    /**
     * 创建自定义线程池
     * @param customThreadPoolConfig 线程池配置
     * @param threadNamePrefix 线程池创建的线程名称前缀
     * @param daemon 是否指定为守护线程
     * @return 线程池对象
     */
    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(
                customThreadPoolConfig.getCorePoolSize(),
                customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(),
                customThreadPoolConfig.getTimeUnit(),
                customThreadPoolConfig.getWorkQueue(),
                threadFactory
        );
    }

    /**
     * 创建ThreadFactory
     * 1. threadNamePrefix不为空则使用自定义ThreadFactory
     * 2. 否则使用defaultThreadFactory
     * @param threadNamePrefix 线程池创建的线程名称前缀
     * @param daemon 是否指定为守护线程
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            // 创建守护线程池工厂
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                // 创建普通线程池工厂
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        // 创建默认线程池工厂
        return Executors.defaultThreadFactory();
    }

    /**
     * 关闭所有线程池
     */
    public static void showDownAllThreadPool() {
        log.info("调用 shutDownAllThreadPool 方法");
        // 开始关闭所有线程池
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            // 得到线程池对象
            ExecutorService executorService = entry.getValue();
            // 尝试关闭线程池
            executorService.shutdown();
            log.info("关闭 [{}][{}]", entry.getKey(), executorService.isTerminated());
            try {
                // 阻塞10秒等待线程池终止
                boolean terminationResult = executorService.awaitTermination(10, TimeUnit.SECONDS);
                if (terminationResult) {
                    log.info("线程池已终止");
                } else {
                    log.error("线程池没有终止");
                }
            } catch (InterruptedException e) {
                log.error("线程池没有终止");
                // 立即关闭线程池
                executorService.shutdownNow();
            }
        });
    }

    /**
     * 打印线程池状态
     * @param threadPool 线程池对象
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
