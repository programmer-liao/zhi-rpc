package com.dezhi.simple.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;

/**
 * @author liaodezhi
 * @date 2023/1/29
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    /**
     * @param registry BeanDefinitionRegistry
     * @param annoType Annotation class对象
     */
    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(@Nonnull String... basePackages) {
        return super.scan(basePackages);
    }
}