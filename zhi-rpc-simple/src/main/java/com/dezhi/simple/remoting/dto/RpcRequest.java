package com.dezhi.simple.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * Rpc请求传输对象
 * @author liaodezhi
 * @date 2023/1/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 序列化版本
     */
    private static final long serialVersionUID = -8447097808679910477L;

    /**
     * 请求Id
     */
    private String requestId;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 参数列表
     */
    private Object[] parameters;

    /**
     * 服务版本
     */
    private String version;

    /**
     * 服务组
     */
    private String group;

    /**
     * 获取rpc服务相关信息
     * @return rpc服务相关信息
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
