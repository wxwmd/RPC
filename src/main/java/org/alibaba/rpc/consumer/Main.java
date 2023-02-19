package org.alibaba.rpc.consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println(name);
        }

        RpcClientService rpcClientService = context.getBean(RpcClientService.class);
        rpcClientService.test();
    }
}
