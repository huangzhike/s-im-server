package mmp.im.auth.handler.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mmp.im.auth.protocol.ProtocolParserHolder;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// @Component
@ChannelHandler.Sharable
public class GateToAuthHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        Channel channel = channelHandlerContext.channel();

        ParserPacket parserPacket = (ParserPacket) message;

        IProtocolParser protocolParser = ProtocolParserHolder.get(parserPacket.getProtocolType());

        LOG.warn("协议类型... {}", parserPacket.getProtocolType());

        if (protocolParser != null) {
            protocolParser.parse(channelHandlerContext, parserPacket.getBody());
            LOG.warn("GateToAuthHandler channelRead parserPacket  {} remoteAddress {}", parserPacket, channel.remoteAddress());
        } else if (parserPacket.getProtocolType() == ProtocolHeader.ProtocolType.HEARTBEAT.getType()) {
            LOG.warn("GateToAuthHandler channelRead heartbeat... parserPacket  {} remoteAddress {}", parserPacket, channel.remoteAddress());
        } else {
            LOG.warn("无法识别，通道关闭");
            // channel.close();
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

        String key = channelHandlerContext.channel().remoteAddress().toString();
        // 移除连接
        ConnectionHolder.removeServerConnection(key);
        LOG.warn("channelInactive... remoteAddress: " + channel.remoteAddress());
        // 关闭连接
        if (channel.isOpen()) {
            // channel.close();
            LOG.warn("channelInactive... isOpen remoteAddress: " + channel.remoteAddress());

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

