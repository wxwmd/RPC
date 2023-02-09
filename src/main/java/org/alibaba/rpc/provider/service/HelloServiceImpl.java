package org.alibaba.rpc.provider.service;

public class HelloServiceImpl implements HelloService{
    public String Hello(String name) {
        return "Hello, " + name;
    }
}
