package org.alibaba.nettylearn.common.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alibaba.nettylearn.common.bean.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RpcResponseDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponseDecoder.class);

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();
        byte[] responseBytes = new byte[length];
        in.readBytes(responseBytes, 0, length);
        RpcResponse rpcResponse = JSON.parseObject(responseBytes, RpcResponse.class);
        logger.info("get rpc response: " + rpcResponse.getResult());
        out.add(rpcResponse);
    }
}