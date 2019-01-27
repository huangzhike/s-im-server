package mmp.im.client.connector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;


@Component
@ChannelHandler.Sharable
public class ClientToGateConnectorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Autowired
    @Qualifier("gateToDistConnectorProtocolParserHolder")
    private ProtocolParserHolder protocolParserHolder;


    @Autowired
    private ConnectorChannelHolder connectorChannelHolder;


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        Channel channel = channelHandlerContext.channel();

        ParserPacket parserPacket = (ParserPacket) message;

        IProtocolParser protocolParser = protocolParserHolder.get(parserPacket.getProtocolType());

        if (protocolParser != null) {
            protocolParser.parse(channelHandlerContext, parserPacket.getBody());
            LOG.warn("channelRead parserPacket... {} remoteAddress... {}", parserPacket, channel.remoteAddress());
        } else {
            // channel.close();
            LOG.warn("无法识别，通道关闭");
        }

        // channel.writeAndFlush(null).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

    }


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelActive... remoteAddress... {} ", channelHandlerContext.channel().remoteAddress());
        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        Channel channel = channelHandlerContext.channel();

        if (channel != null) {
            SocketAddress socketAddress = channel.remoteAddress();
            if (socketAddress != null) {
                String key = socketAddress.toString();
                LOG.warn("channelInactive... remove remoteAddress... {}", channel.remoteAddress());

                connectorChannelHolder.setChannelHandlerContext(null);

            }
            // 关闭连接
            if (channel.isOpen()) {
                // channel.close();
                LOG.warn("channelInactive... close remoteAddress... {}", channel.remoteAddress());
            }
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

