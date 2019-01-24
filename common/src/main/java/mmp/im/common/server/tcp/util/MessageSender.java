package mmp.im.common.server.tcp.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.server.tcp.cache.acknowledge.ResendMessage;
import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageSender {


    private final static Logger LOG = LoggerFactory.getLogger(MessageSender.class);


    public static void reply(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageLite messageLite = (MessageLite) object;
        channelHandlerContext.channel().writeAndFlush(messageLite);
        toBeAcknowledgedIfNeed(channelHandlerContext, object);

    }


    public static void sendToClient(String to, Object object) {

        MessageLite messageLite = (MessageLite) object;
        // 连在同一个Gate
        ChannelHandlerContext channelHandlerContext = ConnectionHolder.getClientConnection(to);
        // 发送
        channelHandlerContext.channel().writeAndFlush(messageLite);
        LOG.warn("sendToClient... {}", messageLite);
        // 需要ACK
        toBeAcknowledgedIfNeed(channelHandlerContext, object);
    }


    public static void sendToServers(Object object) {

        MessageLite messageLite = (MessageLite) object;
        List<ChannelHandlerContext> serverChannelHandlerContextList = ConnectionHolder.getServerConnectionList();

        // 发给中心server转发
        if (serverChannelHandlerContextList != null && serverChannelHandlerContextList.size() != 0) {
            serverChannelHandlerContextList.forEach(channelHandlerContext -> {
                channelHandlerContext.channel().writeAndFlush(messageLite);
                LOG.warn("sendToServers... {}", messageLite);
                toBeAcknowledgedIfNeed(channelHandlerContext, object);
            });
        }

    }


    public static void sendToServer(String key, Object object) {

        MessageLite messageLite = (MessageLite) object;
        ChannelHandlerContext channelHandlerContext = ConnectionHolder.getServerConnection(key);
        if (channelHandlerContext != null) {
            channelHandlerContext.channel().writeAndFlush(messageLite);
            LOG.warn("sendToServer... {}", messageLite);
            toBeAcknowledgedIfNeed(channelHandlerContext, object);
        }

    }


    private static void toBeAcknowledgedIfNeed(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageLite messageLite = (MessageLite) object;

        if (messageLite instanceof MessageTypeA.Message) {
            LOG.warn("messageLite... instanceof MessageTypeA.Message...");
            MessageTypeA.Message msg = (MessageTypeA.Message) messageLite;
            ResendMessageMap.put(msg.getSeq(), new ResendMessage(messageLite, channelHandlerContext, msg.getSeq()));
        }

    }

}
