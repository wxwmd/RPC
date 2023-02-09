package org.alibaba.rpc.client;

import org.alibaba.rpc.server.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);
    public static void main(String[] args) {
        HelloService service = ServiceProxy.createService(HelloService.class, "1");
        String result = service.Hello("wei");
        logger.info(String.format("final result: %s", result));
    }
}
