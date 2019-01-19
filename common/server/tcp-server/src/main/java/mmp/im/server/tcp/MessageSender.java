package mmp.im.server.tcp;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.server.tcp.cache.ack.ResendMessage;
import mmp.im.server.tcp.cache.ack.ResendMessageMap;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;

import java.util.List;

public class MessageSender {
    private static MQPublisher mqPublisher;

    static {

        mqPublisher = new MQPublisher("", "");
        mqPublisher.start();
    }


    public static void sendToClient(String to, Object msg) {

        MessageLite messageLite = (MessageLite) msg;
        ChannelHandlerContext clientChannelHandlerContext = ConnectionHolder.getClientConnection(to);

        // 连在同一个Gate
        clientChannelHandlerContext.channel().writeAndFlush(msg);
        final Long s = SeqGenerator.get();

        ResendMessageMap.put(s, new ResendMessage(messageLite, clientChannelHandlerContext.channel(), s));


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
            });


        }


    }

}
