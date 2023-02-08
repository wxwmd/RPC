package org.alibaba.rpc.server.service;

public class HelloServiceImpl implements HelloService{
    public String Hello(String name) {
        return "Hello, " + name;
    }
}
