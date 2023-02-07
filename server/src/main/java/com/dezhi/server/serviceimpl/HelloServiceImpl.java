package com.dezhi.server.serviceimpl;

import com.dezhi.api.Hello;
import com.dezhi.api.HelloService;
import com.dezhi.simple.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
@Slf4j
@RpcService
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}", hello.getMessage());
        String result = "Hello description = " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}", result);
        return result;
    }
}
