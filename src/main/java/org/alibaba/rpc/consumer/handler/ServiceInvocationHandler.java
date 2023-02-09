package org.alibaba.rpc.consumer.handler;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.alibaba.rpc.common.bean.RpcRequest;
import org.alibaba.rpc.common.bean.RpcResponse;
import org.alibaba.rpc.common.bean.ServiceInfo;
import org.alibaba.rpc.consumer.connection.ChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ServiceInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);

    private Class className;
    
    public ServiceInvocationHandler(Class className){
        this.className = className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info(String.format("invoke method %s of class %s", method.getName(), proxy.getClass().toString()));
        RpcResponse response = null;
        try{
            // 根据serviceInfo获取和服务提供者之间的channel
            ServiceInfo serviceInfo = new ServiceInfo(className.getName(), method.getName(), args);
            Channel channel = ChannelManager.getChannelByServiceInfo(serviceInfo);
            // 包装 RpcRequest
            String requestId = UUID.randomUUID().toString();
            RpcRequest request = new RpcRequest(requestId, "org.alibaba.rpc.provider.service.HelloServiceImpl", method.getName(), args);
            // 通过channel将RpcRequest写出
            channel.writeAndFlush(request);
            Promise<RpcResponse> promise = new DefaultPromise<RpcResponse>(channel.eventLoop());
            RpcResponseHandler.enrollPromises(requestId, promise);
            promise.await();
            response = promise.getNow();
            //channelFuture.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }
        return  response.getResult();
    }
}
