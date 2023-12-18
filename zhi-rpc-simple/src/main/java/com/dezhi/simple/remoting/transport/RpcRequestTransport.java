package com.dezhi.simple.remoting.transport;

import com.dezhi.common.extension.SPI;
import com.dezhi.simple.remoting.dto.RpcRequest;

/**
 * Rpc请求传输
 * @author liaodezhi
 * @date 2023/1/30
 */
@SPI
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
