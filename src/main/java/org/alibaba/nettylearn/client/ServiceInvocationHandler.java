package org.alibaba.nettylearn.client;

import io.netty.bootstrap.Bootstrap;
import org.alibaba.nettylearn.common.codec.RpcRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);

    private Class interfaceName;
    private String version;

    private Bootstrap bootstrap;

    public ServiceInvocationHandler(Class interfaceName, String version){
        this.interfaceName = interfaceName;
        this.version = version;


    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info(String.format("invoke method %s of class %s", method.getName(), proxy.getClass().toString()));
        return null;
    }
}
