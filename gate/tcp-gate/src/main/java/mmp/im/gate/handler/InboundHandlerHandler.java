package mmp.im.gate.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.*;
import mmp.im.gate.protocol.parser.IProtocolParser;
import mmp.im.gate.protocol.parser.ProtocolParserHolder;
import mmp.im.protocol.Acknowledge;

@ChannelHandler.Sharable
// public class InboundHandlerHandler extends SimpleChannelInboundHandler<Object> {
public class InboundHandlerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        Channel channel = ctx.channel();

        byte[] bytes = (byte[]) message;


        IProtocolParser protocolParser = ProtocolParserHolder.get(bytes[0]);

        if (protocolParser != null) {
            byte[] b = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, b, 0, b.length);
            protocolParser.parse(ctx, b);
        }

        channel.writeAndFlush(null).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
