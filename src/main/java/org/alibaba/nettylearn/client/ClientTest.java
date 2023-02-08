package org.alibaba.nettylearn.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.alibaba.nettylearn.common.bean.RpcRequest;
import org.alibaba.nettylearn.common.codec.RpcRequestEncoder;
import org.alibaba.nettylearn.common.codec.RpcResponseDecoder;
import org.alibaba.nettylearn.server.service.HelloServiceImpl;

public class ClientTest {
    public static void main(String[] args) {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcResponseDecoder());
                            ch.pipeline().addLast(new RpcClientHandler());
                            ch.pipeline().addLast(new RpcRequestEncoder());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();


            RpcRequest request = new RpcRequest("org.alibaba.nettylearn.server.service.HelloServiceImpl", "Hello", new Object[]{"wei"});
            channelFuture.channel().writeAndFlush(request);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
