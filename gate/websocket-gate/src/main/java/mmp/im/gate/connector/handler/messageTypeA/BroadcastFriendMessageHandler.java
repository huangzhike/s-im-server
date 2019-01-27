package mmp.im.gate.connector.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastFriendMessageHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.BROADCAST_FRIEND_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageTypeA.Message.FriendMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.FriendMessage.class);
            LOG.warn("receive FriendMessage... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder.buildFriendMessage(message.getFrom(), message.getTo(), msg);
        // 单聊消息
        SpringContextHolder.getBean(MessageSender.class).sendToConnector(m, message.getTo());

        // 回复确认
        SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

    }
}

