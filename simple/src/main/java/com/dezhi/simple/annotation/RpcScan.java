package com.dezhi.simple.annotation;

import com.dezhi.simple.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 作用: 扫描自定义注解
 * @author liaodezhi
 * @date 2023/1/29
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
@Documented
public @interface RpcScan {

     String[] basePackage();
}
