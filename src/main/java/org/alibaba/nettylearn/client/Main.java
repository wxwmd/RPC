package org.alibaba.nettylearn.client;

import org.alibaba.nettylearn.server.service.HelloService;
import org.alibaba.nettylearn.server.service.HelloServiceImpl;

public class Main {
    public static void main(String[] args) {
        HelloService service = ServiceProxy.createService(HelloService.class);
        service.Hello("wei");
    }
}
