package org.alibaba.rpc.consumer;

import org.alibaba.rpc.common.spring.SpringBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    public SpringBeanPostProcessor springBeanPostProcessor(){
        return new SpringBeanPostProcessor();
    }
}
