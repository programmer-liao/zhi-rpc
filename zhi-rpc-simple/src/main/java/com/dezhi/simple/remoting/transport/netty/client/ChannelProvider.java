package com.dezhi.simple.remoting.transport.netty.client;


import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存取Channel
 * @author liaodezhi
 * @date 2023/2/2
 */
@Slf4j
public class ChannelProvider {

    /**
     * 用于存放channel
     * key: String
     * value: Channel
     */
    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }
    public Channel get(InetSocketAddress inetSocketAddress) {
        // 获得key
        String key = inetSocketAddress.toString();
        // 如果map中包含key
        if (channelMap.containsKey(key)) {
            // 获得channel
            Channel channel = channelMap.get(key);
            // 如果channel不为空并且channel处于活跃状态
            if (channel != null && channel.isActive()) {
                // 返回channel
                return channel;
            } else {
                // 否则就移除channel
                channelMap.remove(key);
            }
        }
        return null;
    }

    /**
     * 向channelMap中添加channel
     * @param inetSocketAddress 地址
     * @param channel channel
     */
    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        // 获取key
        String key = inetSocketAddress.toString();
        // 放入map中
        channelMap.put(key, channel);
    }

    /**
     * 移除channel
     * @param inetSocketAddress 地址
     */
    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // 移除
        channelMap.remove(key);
        log.info("channelMap当前大小: [{}]", channelMap.size());
    }
}
