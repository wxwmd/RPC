package org.alibaba.nettylearn.common.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.alibaba.nettylearn.common.bean.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestEncoder.class);

    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        byte[] bytes = JSON.toJSONString(msg).getBytes();
        // 写入内容长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);

        logger.info("encode rpc request");
    }
}
