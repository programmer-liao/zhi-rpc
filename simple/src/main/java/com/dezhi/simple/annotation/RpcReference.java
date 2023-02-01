package com.dezhi.simple.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * 版本
     */
    String version() default "";

    /**
     * 组
     */
    String group() default "";
}
