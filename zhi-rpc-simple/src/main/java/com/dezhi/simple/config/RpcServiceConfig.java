package com.dezhi.simple.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RpcService配置
 *
 * @author liaodezhi
 * @date 2023/1/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class RpcServiceConfig {

    /**
     * 服务版本
     */
    private String version = "";

    /**
     * 当一个接口有多个实现类时, 用组区分
     */
    private String group = "";


    /**
     * 目标服务
     */
    private Object service;

    /**
     * 获取rpc服务名称
     * @return rpc服务名称
     */
    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    /**
     * 获取服务名称
     * @return 服务名称
     */
    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
