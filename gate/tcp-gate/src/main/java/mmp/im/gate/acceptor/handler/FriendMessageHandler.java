package mmp.im.gate.acceptor.handler;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;



public class FriendMessageHandler   implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = FriendMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }



    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        FriendMessage message = (FriendMessage) object;

        // Message m = (Message) MessageBuilder.buildFriendMessage(message.getFrom(), message.getTo(), msg);
        // 转发单聊消息
        // SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);

        // 回复确认
        // SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

    }
}

