package com.dezhi.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author liaodezhi
 * @date 2023/2/7
 */
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
