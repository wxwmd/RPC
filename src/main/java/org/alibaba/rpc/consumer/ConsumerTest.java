package org.alibaba.rpc.consumer;

import org.alibaba.rpc.consumer.handler.ServiceInvocationHandler;
import org.alibaba.rpc.consumer.proxy.ServiceProxy;
import org.alibaba.rpc.provider.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConsumerTest {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);
    public static void main(String[] args) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 5, 5L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        for (int i=0;i<10;i++){
            threadPool.execute(()->{
                HelloService service = ServiceProxy.createService(HelloService.class);
                String result = service.Hello("wei");
                logger.info(String.format("final result: %s", result));
            });
        }
    }
}
