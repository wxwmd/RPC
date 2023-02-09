package org.alibaba.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.alibaba.rpc.common.bean.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);
    private static ConcurrentHashMap<String, Promise<RpcResponse>> promiseMap;
    private static final Object LOCK = new Object();

    private static void initPromiseMap(){
        synchronized (LOCK){
            if (promiseMap == null){
                promiseMap = new ConcurrentHashMap<String, Promise<RpcResponse>>();
            }
        }
    }

    public static void enrollPromises(String requestId, Promise<RpcResponse> promise){
        if (promiseMap == null){
            initPromiseMap();
        }
        promiseMap.put(requestId, promise);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        String responseId = msg.getResponseId();
        logger.info(String.format("get response for %s", msg.getResponseId()));

        if (promiseMap.containsKey(responseId)){
            Promise<RpcResponse> promise = promiseMap.get(responseId);
            promise.setSuccess(msg);
        }
    }
}
