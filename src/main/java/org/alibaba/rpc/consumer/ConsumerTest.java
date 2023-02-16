package org.alibaba.rpc.consumer;

import org.alibaba.rpc.consumer.proxy.ServiceProxy;
import org.alibaba.rpc.provider.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerTest {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerTest.class);
    public static void main(String[] args) {
        HelloService service = ServiceProxy.createService(HelloService.class, "1.0");
        String result = service.Hello("wei");
        logger.info(String.format("final result: %s", result));

        try {
            Thread.sleep(3000);
            result = service.Hello("miduo");
            logger.info(String.format("final result: %s", result));

            Thread.sleep(11000);
            result = service.Hello("doris");
            logger.info(String.format("final result: %s", result));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
