package com.dezhi.simple.spring;

import com.dezhi.common.extension.ExtensionLoader;
import com.dezhi.common.factory.SingletonFactory;
import com.dezhi.simple.annotation.RpcReference;
import com.dezhi.simple.annotation.RpcService;
import com.dezhi.simple.config.RpcServiceConfig;
import com.dezhi.simple.provider.ServiceProvider;
import com.dezhi.simple.provider.impl.ZkServiceProviderImpl;
import com.dezhi.simple.proxy.RpcClientProxy;
import com.dezhi.simple.remoting.transport.RpcRequestTransport;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

/**
 * 调用这个方法创建bean如果这个类被自定义注解所注解
 * @author liaodezhi
 * @date 2023/1/29
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    /**
     * 服务提供者
     */
    private final ServiceProvider serviceProvider;

    /**
     * 服务传输
     */
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        // 通过单例生成
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        //通过扩展加载器加载
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] 被 [{}] 注解", bean.getClass().getName(), RpcService.class.getCanonicalName());
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                // 获取代理客户端
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                // 反射爆破
                declaredField.setAccessible(true);
                try {
                    // 设置属性
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        // 返回bean
        return bean;
    }
}
