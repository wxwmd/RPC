package org.alibaba.rpc.common.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.alibaba.rpc.common.bean.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponseEncoder.class);

    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {
        byte[] bytes = JSON.toJSONBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        logger.info("encode rpc response: "+msg.getResult());
    }
}
