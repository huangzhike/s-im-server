package mmp.im.server.tcp;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.server.tcp.cache.ack.ResendMessage;
import mmp.im.server.tcp.cache.ack.ResendMessageMap;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;

import java.util.List;

public class MessageSender {


    public static void send(String to, Object msg) {

        MessageLite messageLite = (MessageLite) msg;
        ChannelHandlerContext clientChannelHandlerContext = ConnectionHolder.getClientConnection(to);
        // 连在同一个Gate

        if (clientChannelHandlerContext != null) {
            clientChannelHandlerContext.channel().writeAndFlush(msg);
            Long seq = SeqGenerator.get();
            // 待确认
            ResendMessageMap.put(seq, new ResendMessage(messageLite, clientChannelHandlerContext.channel(), seq));

        }

        List<ChannelHandlerContext> serverChannelHandlerContextList = ConnectionHolder.getServerConnectionList();
        // 发给中心server转发
        serverChannelHandlerContextList.forEach(channelHandlerContext -> {
            channelHandlerContext.channel().writeAndFlush(msg);
            final Long s = SeqGenerator.get();

            ResendMessageMap.put(s, new ResendMessage(messageLite, clientChannelHandlerContext.channel(), s));

        });

        //  MQ离线处理


    }

}
