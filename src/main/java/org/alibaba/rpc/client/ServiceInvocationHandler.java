package org.alibaba.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.alibaba.rpc.common.bean.RpcRequest;
import org.alibaba.rpc.common.bean.RpcResponse;
import org.alibaba.rpc.common.codec.RpcRequestEncoder;
import org.alibaba.rpc.common.codec.RpcResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ServiceInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);

    private Class interfaceName;
    private String version;

    private EventLoopGroup eventExecutors;
    private Bootstrap bootstrap;

    public ServiceInvocationHandler(Class interfaceName, String version){
        this.interfaceName = interfaceName;
        this.version = version;

        eventExecutors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new RpcResponseDecoder());
                        ch.pipeline().addLast(new RpcResponseHandler());
                        ch.pipeline().addLast(new RpcRequestEncoder());
                    }
                });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info(String.format("invoke method %s of class %s", method.getName(), proxy.getClass().toString()));

        // 先写死，这部分以后应该要从zookeeper中获取
        String serverAddress = "127.0.0.1";
        int serverPort = 8080;
        ChannelFuture channelFuture = bootstrap.connect(serverAddress, serverPort).sync();
        RpcResponse response;
        try{
            String requestId = UUID.randomUUID().toString();
            RpcRequest request = new RpcRequest(requestId, "org.alibaba.rpc.server.service.HelloServiceImpl", method.getName(), args);
            channelFuture.channel().writeAndFlush(request);
            Promise<RpcResponse> promise = new DefaultPromise<RpcResponse>(channelFuture.channel().eventLoop());
            RpcResponseHandler.enrollPromises(requestId, promise);
            promise.await();
            response = promise.getNow();
            logger.info("promise fire: "+response.toString());

//            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
        return  response.getResult();
    }
}
