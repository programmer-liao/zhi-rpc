package com.dezhi.server.serviceimpl;

import com.dezhi.api.Hello;
import com.dezhi.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
@Slf4j
public class HelloServiceImpl2 implements HelloService {
    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}", hello.getMessage());
        String result = "Hello description = " + hello.getDescription();
        log.info("HelloServiceImp2l返回: {}", result);
        return result;
    }
}
