package com.dezhi.simple.registry.zk.util;

import com.dezhi.common.enums.RpcConfigEnum;
import com.dezhi.common.util.PropertiesFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Curator(zookeeper client)工具类
 *
 * @author liaodezhi
 * @date 2023/1/30
 */
@Slf4j
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

    /**
     * zookeeper客户端接口
     */
    private static CuratorFramework zkClient;

    /**
     * zookeeper默认地址
     */
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    /**
     * 创建永久节点
     *
     * @param zkClient zookeeper客户端
     * @param path     节点路径
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            // 节点已经存在
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("节点已经存在, 节点是: [{}]", path);
            } else {
                // 节点不存在, 创建节点
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点创建成功, 节点为: [{}]", path);
            }
        } catch (Exception e) {
            log.error("创建永久节点 [{}] 失败", path);
        }
    }

    /**
     * 获取某节点下的子节点
     *
     * @param zkClient       zookeeper客户端
     * @param rpcServiceName rpc服务名称
     * @return 子节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        // Map中存在数据, 直接返回
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        // 没有就查询zookeeper后返回并存入Map
        List<String> result = null;
        String servicePath = ZK_RESISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            // 查询
            result = zkClient.getChildren().forPath(servicePath);
            // 结果放入Map
            SERVICE_ADDRESS_MAP.put(servicePath, result);
            // 注册监听器
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("获取[{}]节点下的子节点失败", servicePath);
        }
        // 返回
        return result;
    }

    /**
     * 清空zookeeper数据
     *
     * @param zkClient          zookeeper客户端
     * @param inetSocketAddress 地址
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        // 遍历删除
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("清空zookeeper数据失败, 失败路径: [{}]", p);
            }
        });
        // 清除成功
        log.info("所有注册服务的数据已经都被清理[{}]", REGISTERED_PATH_SET);
    }

    /**
     * 获取zkClient
     * @return zkClient
     */
    public static CuratorFramework getZkClient() {
        // 如果zkClient已经存在并且状态为STARTED, 直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // 读取配置文件
        Properties properties = PropertiesFileUtils.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress;
        // 如果配置文件不为空并且ZK_ADDRESS属性值存在
        if ((properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null)) {
            zookeeperAddress = properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue());
        } else {
            // 不存在则使用默认地址
            zookeeperAddress = DEFAULT_ZOOKEEPER_ADDRESS;
        }

        // 重试策略
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        // 启动zkClient
        zkClient.start();
        try {
            // 等待30秒直到连接到zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("zookeeper连接超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    /**
     * 注册监听服务到具体的节点上
     * @param rpcServiceName 服务名称
     * @param zkClient zookeeper客户端
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_RESISTER_ROOT_PATH + "/" + rpcServiceName;
        // 子节点缓存
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        // 子节点缓存监听器
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        // 添加子节点缓存监听器
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        // 启动子节点缓存服务
        pathChildrenCache.start();
    }
}

