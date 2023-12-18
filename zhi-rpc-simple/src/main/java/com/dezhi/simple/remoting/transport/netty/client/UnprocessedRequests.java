package com.dezhi.simple.remoting.transport.netty.client;

import com.dezhi.simple.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理请求
 * @author liaodezhi
 * @date 2023/2/2
 */
public class UnprocessedRequests {

    /**
     * key: String
     * value: CompletableFuture
     */
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    /**
     * 放置未处理请求
     * @param requestId 请求Id
     * @param future CompletableFuture
     */
    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        // 放入map中
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * 完成动作
     * @param rpcResponse rpc响应
     */
    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        // future不为空, 就完成CompletableFuture
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            // 否则抛出非法状态异常
            throw new IllegalStateException();
        }
    }
}
