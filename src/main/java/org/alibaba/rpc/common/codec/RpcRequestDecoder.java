package org.alibaba.rpc.common.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alibaba.rpc.common.bean.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RpcRequestDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestDecoder.class);

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 消息长度
        int length = in.readInt();

        // 读取消息
        byte[] msgBytes = new byte[length];
        in.readBytes(msgBytes, 0, length);
        RpcRequest rpcRequest = JSON.parseObject(msgBytes, RpcRequest.class);
        logger.info(String.format("get rpc request: invoke class: %s, method: %s", rpcRequest.getClassName(), rpcRequest.getMethodName()));
        out.add(rpcRequest);
    }
}
