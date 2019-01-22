package mmp.im.common.server.tcp.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.server.tcp.cache.ack.ResendMessage;
import mmp.im.common.server.tcp.cache.ack.ResendMessageMap;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageSender {


    private final static Logger LOG = LoggerFactory.getLogger(MessageSender.class);


    public static void sendToClient(String to, Object msg) {

        MessageLite messageLite = (MessageLite) msg;
        ChannelHandlerContext clientChannelHandlerContext = ConnectionHolder.getClientConnection(to);


        // 连在同一个Gate
        clientChannelHandlerContext.channel().writeAndFlush(msg);
        final Long s = SeqGenerator.get();

        ResendMessageMap.put(s, new ResendMessage(messageLite, clientChannelHandlerContext.channel(), s));

        LOG.warn("MessageSender sendToClient -> {}", msg);

    }


    public static void sendToServer(Object msg) {

        MessageLite messageLite = (MessageLite) msg;
        List<ChannelHandlerContext> serverChannelHandlerContextList = ConnectionHolder.getServerConnectionList();

        // 发给中心server转发

        if (serverChannelHandlerContextList != null && serverChannelHandlerContextList.size() != 0) {

            serverChannelHandlerContextList.forEach(channelHandlerContext -> {
                channelHandlerContext.channel().writeAndFlush(msg);
                Long seq = SeqGenerator.get();
                // 待确认
                ResendMessageMap.put(seq, new ResendMessage(messageLite, channelHandlerContext.channel(), seq));


                LOG.warn("MessageSender sendToServer list -> {}", msg);

            });


        }


    }


    public static void sendToServer(String key, Object msg) {

        MessageLite messageLite = (MessageLite) msg;
        ChannelHandlerContext serverChannelHandlerContext = ConnectionHolder.getServerConnection(key);


        if (serverChannelHandlerContext != null) {

            serverChannelHandlerContext.channel().writeAndFlush(msg);
            Long seq = SeqGenerator.get();
            // 待确认
            ResendMessageMap.put(seq, new ResendMessage(messageLite, serverChannelHandlerContext.channel(), seq));

            LOG.warn("MessageSender sendToServer -> {}", msg);

        }


    }

}
