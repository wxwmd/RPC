package org.alibaba.rpc.client;

import org.alibaba.rpc.server.service.HelloService;

public class ClientTest {
    public static void main(String[] args) {
        HelloService service = ServiceProxy.createService(HelloService.class, "1");
        service.Hello("wei");
    }
}
