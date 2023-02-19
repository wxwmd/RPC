package org.alibaba.rpc.provider.service;

import org.springframework.stereotype.Component;

@Component
public class HelloServiceImpl implements HelloService{
    public String Hello(String name) {
        return "Hello, " + name;
    }
}
