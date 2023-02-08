package org.alibaba.nettylearn.server.service;

public class HelloServiceImpl implements HelloService{
    public String Hello(String name) {
        return "Hello, " + name;
    }
}
