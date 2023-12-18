package com.dezhi.client;

import com.dezhi.api.Hello;
import com.dezhi.api.HelloService;
import com.dezhi.simple.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
