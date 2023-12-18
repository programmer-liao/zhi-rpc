package com.dezhi.simple.config;

import com.dezhi.common.util.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.dezhi.simple.registry.zk.util.CuratorUtils;
import com.dezhi.simple.remoting.transport.netty.server.NettyRpcServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 自定义关闭钩子
 * @author liaodezhi
 * @date 2023/2/4
 */
@Slf4j
public class CustomShutdownHook {

    /**
     * 钩子
     */
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    /**
     * 获取Custom_SHUTDOWN_HOOK
     * @return Custom_SHUTDOWN_HOOK
     */
    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }
    /**
     * 清除所有的服务节点
     */
    public void clearAll() {
      log.info("添加ShutdownHook clearAll钩子");
      // 异步调用
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
              // 服务地址
              InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
              // 清除所有的注册节点
              CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
          } catch (UnknownHostException ignored) {

          }
          // 关闭所有的线程池
          ThreadPoolFactoryUtils.shutDownAllThreadPool();
      }));
    }
}
