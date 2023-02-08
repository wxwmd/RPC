package org.alibaba.nettylearn.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.alibaba.nettylearn.common.bean.RpcRequest;
import org.alibaba.nettylearn.common.bean.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        String className = msg.getClassName();
        String methodName = msg.getMethodName();
        Object[] params = msg.getParams();

        // 反射得到类
        Class cls = Class.forName(className);
        // 得到方法的所有参数，为了调用方法（因为java具有重载，无法根据方法名确定唯一的方法）
        Class[] paramClass = new Class[params.length];
        for (int i = 0; i< params.length;i++){
            paramClass[i] = params[i].getClass();
        }
        Method method = cls.getDeclaredMethod(methodName, paramClass);
        Object result = method.invoke(cls.newInstance(), params);
        RpcResponse rpcResponse = new RpcResponse(result);
        logger.info("rpc result: "+result);
        ctx.writeAndFlush(rpcResponse);
    }
}
