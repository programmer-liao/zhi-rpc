package com.dezhi.simple.registry.zk.util;

import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Curator(zookeeper client)工具类
 * @author liaodezhi
 * @date 2023/1/30
 */
public class CuratorUtils {

    /**
     * 默认睡眠时间
     */
    private static final int BASE_SLEEP_TIME = 1000;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 3;

    /**
     * 服务注册根路径
     */
    public static final String ZK_RESISTER_ROOT_PATH = "zhi-rpc";

    /**
     * 服务地址Map
     * key - 服务名称
     * value - 地址列表
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    /**
     * 已经注册的服务节点地址Set
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    private static Curat
}
