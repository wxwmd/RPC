package org.alibaba.rpc.provider.register;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.alibaba.rpc.common.bean.ServiceInfo;
import org.alibaba.rpc.common.bean.ServiceProvider;
import org.alibaba.rpc.common.codec.RpcRequestDecoder;
import org.alibaba.rpc.common.codec.RpcResponseEncoder;
import org.alibaba.rpc.common.zk.CuratorClient;
import org.alibaba.rpc.provider.handler.RpcServerHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcRegister {

    /**
     * 比方说一台机器，通过127.0.0.1:8080 和127.0.0.1：8081提供了两个service
     * 那么就需要开启两个server channel监听rpc请求
     * 相反地， 如果一台机器使用127.0.0.1:8080提供两个服务，我们则只需要开启一个channel，通过多路复用节省资源
     * 因此，使用此数据结构来存放服务地址与channel之间的对应关系
     */
    private static ConcurrentHashMap<ServiceProvider, Channel> serviceChannels;

    static {
        serviceChannels = new ConcurrentHashMap<>();
    }

    /**
     * 将provider上的服务注册到zookeeper上
     * @param serviceProvider 服务提供者
     * @param zkConnectString zookeeper的服务地址 例如127.0.0.1:2181
     * @param serviceInfo 服务信息
     * @return 注册成功true，失败则false
     */
    public static boolean registerService(ServiceProvider serviceProvider, String zkConnectString, ServiceInfo serviceInfo){
        CuratorClient client = new CuratorClient(zkConnectString);
        String zkPath = client.getZkPath(serviceInfo);
        byte[] bytes = JSON.toJSONBytes(serviceProvider);
        try {
            // 如果之前没用在这个地址注册服务，那么需要在此地址上注册channel
            if (!serviceChannels.containsKey(serviceProvider)){
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup);
                bootstrap.channel(NioServerSocketChannel.class);
                bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(10L, 0, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1000, 0, 4, 0, 4));
                        ch.pipeline().addLast(new RpcRequestDecoder());
                        ch.pipeline().addLast(new RpcResponseEncoder());
                        ch.pipeline().addLast(new RpcServerHandler());
                    }
                });
                ChannelFuture channelFuture = bootstrap.bind(serviceProvider.getPort()).sync();
                serviceChannels.put(serviceProvider, channelFuture.channel());
            }

            // 在zk上注册此服务
            client.createPathData(zkPath, bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
