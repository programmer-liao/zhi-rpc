package com.dezhi.simple.annotation;

import java.lang.annotation.*;

/**
 * Rpc服务注解, 作用在服务实现类上
 * @author liaodezhi
 * @date 2023/1/29
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {

    /**
     * 服务版本
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";
}
