package org.alibaba.rpc.provider.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.alibaba.rpc.common.bean.RpcRequest;
import org.alibaba.rpc.common.bean.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel registered");
    }

    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        String className = msg.getClassName();
        String methodName = msg.getMethodName();
        Object[] params = msg.getParams();

        Object result;
        // 反射得到类
        try {
            Class cls = Class.forName(className);
            // 得到方法的所有参数，为了调用方法（因为java具有重载，无法根据方法名确定唯一的方法）
            Class[] paramClass = new Class[params.length];
            for (int i = 0; i< params.length;i++){
                paramClass[i] = params[i].getClass();
            }
            Method method = cls.getDeclaredMethod(methodName, paramClass);
            result = method.invoke(cls.newInstance(), params);
        } catch (ClassNotFoundException e){
            result = String.format("class not found: %s", className);
        } catch (NoSuchMethodException e){
            result = String.format("no such method: %s", methodName);
        }

        // 返回rpc request对应的response
        String requestId = msg.getRequestId();
        RpcResponse rpcResponse = new RpcResponse(requestId, result);
        logger.info("rpc result: "+result);
        ctx.writeAndFlush(rpcResponse);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(idleStateEvent.state())){
                // client channel time out，关闭channel
                ctx.close();
            }
        }
    }
}
