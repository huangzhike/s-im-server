package mmp.im.gate.codec.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import mmp.im.gate.util.serializer.SerializerHolder;
import mmp.im.protocol.Acknowledge;

@ChannelHandler.Sharable
public class AcknowledgeEncoder extends MessageToByteEncoder<Acknowledge> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Acknowledge ack, ByteBuf out) throws Exception {
        byte[] bytes = SerializerHolder.getSerializer().writeObject(ack);
        out.writeBytes(bytes);
    }
}
