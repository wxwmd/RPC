package org.alibaba.rpc.consumer;

import org.alibaba.rpc.common.annotion.RpcReference;
import org.alibaba.rpc.provider.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RpcClientService {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientService.class);

    @RpcReference(version = "1.0")
    private HelloService helloService;

    public void test() {
        String result = helloService.Hello("wei");
        logger.info(String.format("final result: %s", result));
    }
}
