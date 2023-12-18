package com.dezhi.simple.remoting.transport.socket;

import com.dezhi.common.exception.RpcException;
import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.simple.registry.ServiceDiscovery;
import com.dezhi.simple.remoting.dto.RpcRequest;
import com.dezhi.simple.remoting.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 使用Socket实现RpcClient
 * @author liaodezhi
 * @date 2023/2/4
 */
@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements RpcRequestTransport {

    /**
     * 服务发现
     */
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }


    /**
     * 发送请求
     * @param rpcRequest 请求
     * @return 响应
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {
            // 连接
            socket.connect(inetSocketAddress);
            // 传输
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 返回
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw new RpcException("服务调用失败: ", e);
        }
    }
}
