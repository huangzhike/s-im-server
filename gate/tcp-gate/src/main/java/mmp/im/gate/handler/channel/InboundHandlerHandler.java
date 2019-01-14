package mmp.im.gate.handler.channel;

import io.netty.channel.*;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.gate.config.AttributeKeyHolder;
import mmp.im.server.tcp.protocol.parser.IProtocolParser;
import mmp.im.gate.protocol.parser.ProtocolParserHolder;
import mmp.im.protocol.ParserPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
// public class InboundHandlerHandler extends SimpleChannelInboundHandler<Object> {
public class InboundHandlerHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        Channel channel = ctx.channel();

        ParserPacket parserPacket = (ParserPacket) message;


        IProtocolParser protocolParser = ProtocolParserHolder.get(parserPacket.getProtocolType());

        if (protocolParser != null) {
            protocolParser.parse(ctx, parserPacket.getBody());
        } else {
            channel.close();
            LOG.warn("无法识别，通道关闭");
        }

        // channel.writeAndFlush(null).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.warn("channelActive... remoteAddress: " + ctx.channel().remoteAddress());


        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        try {
            String userId = ctx.channel().attr(AttributeKeyHolder.USER_ID).get();
            LOG.warn("channelInactive... userId: " + userId + "remoteAddress: " + ctx.channel().remoteAddress());
            if (null != userId) {
                // 移除连接
                ConnectionHolder.removeConnection(userId);
            }
            // 关闭连接
            if (ctx.channel().isOpen()) {
                ctx.channel().close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        ctx.fireChannelInactive();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

}
