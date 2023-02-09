/**
 * 管理consumer和 众多providers之间的connection
 */

package org.alibaba.rpc.consumer.connection;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.alibaba.rpc.common.bean.ServiceInfo;
import org.alibaba.rpc.common.bean.ServiceProvider;
import org.alibaba.rpc.common.codec.RpcRequestEncoder;
import org.alibaba.rpc.common.codec.RpcResponseDecoder;
import org.alibaba.rpc.consumer.discovery.ServiceDiscovery;
import org.alibaba.rpc.consumer.handler.RpcResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    private static ConcurrentHashMap<ServiceProvider, Channel> providerChannels;
    private static EventLoopGroup eventExecutors;
    private static Bootstrap bootstrap;

    private static Object LOCK = new Object();

    static {
        providerChannels = new ConcurrentHashMap<ServiceProvider, Channel>();
        eventExecutors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1000, 0,4,0,4));
                        ch.pipeline().addLast(new RpcResponseDecoder());
                        ch.pipeline().addLast(new RpcResponseHandler());
                        ch.pipeline().addLast(new RpcRequestEncoder());
                    }
                });
    }

    /**
     * 根据指定的ServiceInfo去获取和服务提供者之间的channel
     * @param serviceInfo rpc服务信息
     * @return consumer与提供此服务的某个provider之间的连接channel
     * @throws InterruptedException
     */
    public static Channel getChannelByServiceInfo(ServiceInfo serviceInfo) throws InterruptedException {
        ServiceProvider serviceProvider = ServiceDiscovery.providerDiscovery(serviceInfo);

        if (!providerChannels.containsKey(serviceProvider)){
            // 初始化channel
            synchronized (LOCK){
                if (!providerChannels.containsKey(serviceProvider)){
                    logger.info(String.format("init the channel to %s:%d", serviceProvider.getAddress(), serviceProvider.getPort()));
                    String serverAddress = serviceProvider.getAddress();
                    int serverPort = serviceProvider.getPort();
                    ChannelFuture channelFuture = bootstrap.connect(serverAddress, serverPort).sync();
                    providerChannels.put(serviceProvider, channelFuture.channel());
                }
            }
        }

        return providerChannels.get(serviceProvider);
    }
}
