package mmp.im.gate.handler.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.gate.protocol.ProtocolParserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// @Component
@ChannelHandler.Sharable
// public class InboundHandlerHandler extends SimpleChannelInboundHandler<Object> {
public class ClientToGateHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        Channel channel = channelHandlerContext.channel();

        ParserPacket parserPacket = (ParserPacket) message;

        IProtocolParser protocolParser = ProtocolParserHolder.get(parserPacket.getProtocolType());

        if (protocolParser != null) {
            protocolParser.parse(channelHandlerContext, parserPacket.getBody());
            LOG.warn("ClientToGateHandler channelRead parserPacket  {} remoteAddress {}", parserPacket, channel.remoteAddress());
        } else {
            // channel.close();
            LOG.warn("无法识别，通道关闭");
        }

        // channel.writeAndFlush(null).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

    }


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelActive... remoteAddress: " + channelHandlerContext.channel().remoteAddress());
        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        Channel channel = channelHandlerContext.channel();
        try {
            String userId = channel.attr(AttributeKeyHolder.CHANNEL_ID).get();
            LOG.warn("channelInactive... userId: " + userId + "remoteAddress: " + channel.remoteAddress());
            if (null != userId) {
                // 移除连接
                ChannelHandlerContext context = ConnectionHolder.removeClientConnection(userId);
                LOG.warn("ClientToGateHandler channelInactive... remove remoteAddress: " + channel.remoteAddress());
            }
            // 关闭连接
            if (channel.isOpen()) {
                LOG.warn("ClientToGateHandler channelInactive... close remoteAddress: " + channel.remoteAddress());
                // channel.close();
            }
        } catch (Exception e) {
            LOG.error("ClientToGateHandler channelInactive... {}", e);
        }

        channelHandlerContext.fireChannelInactive();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        channelHandlerContext.fireExceptionCaught(cause);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        super.userEventTriggered(channelHandlerContext, evt);
    }

}
