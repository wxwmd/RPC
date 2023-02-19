package org.alibaba.rpc.common.spring;

import org.alibaba.rpc.common.annotion.RpcReference;
import org.alibaba.rpc.consumer.proxy.ServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private static Logger logger = LoggerFactory.getLogger(SpringBeanPostProcessor.class);

    /**
     * 扫描所有的bean，
     * 对于任意一个bean，扫描其所有field
     * 如果field被@RpcReference修饰，那么就根据注释的内容，生成代理对象，进行注入
     * @param bean bean
     * @param beanName beanName
     * @return bean
     * @throws BeansException e
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field: declaredFields){
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null){
                String version = rpcReference.version();
                logger.info(String.format("create proxy: %s, version: %s", field.getType(), version));
                Object serviceProxy = ServiceProxy.createService(field.getType(), version);
                field.setAccessible(true);
                try {
                    field.set(bean, serviceProxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return bean;
    }
}
