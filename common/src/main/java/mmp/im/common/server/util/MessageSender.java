package mmp.im.common.server.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.connection.ConnectorChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
@Accessors(chain = true)
public class MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    private static AcceptorChannelManager acceptorChannelMap = AcceptorChannelManager.getInstance();

    private static ConnectorChannelHolder connectorChannelHolder = ConnectorChannelHolder.getInstance();


    public static void reply(ChannelHandlerContext channelHandlerContext, MessageLite messageLite) {
        channelHandlerContext.channel().writeAndFlush(messageLite);
    }

    public static void sendToConnector(MessageLite messageLite, String key) {

        if (acceptorChannelMap != null) {
            ChannelHandlerContext channelHandlerContext = acceptorChannelMap.getChannel(key);
            if (channelHandlerContext != null) {
                sendTo(messageLite, channelHandlerContext);
            }
        }
    }


    public static void sendToAcceptor(MessageLite messageLite) {

        if (connectorChannelHolder != null) {
            ChannelHandlerContext channelHandlerContext = connectorChannelHolder.getChannelHandlerContext();
            if (channelHandlerContext != null) {
                sendTo(messageLite, channelHandlerContext);
            }
        }

    }

    private static void sendTo(MessageLite messageLite, ChannelHandlerContext channelHandlerContext) {
        // 发送
        channelHandlerContext.channel().writeAndFlush(messageLite);
        LOG.warn("sendTo... {}", messageLite);
    }


}
