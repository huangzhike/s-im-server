package mmp.im.gate.connector.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.acceptor.handler.messageTypeA.MessageTypeAHandler;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class BroadcastGroupMessageHandler extends MessageTypeAHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.BROADCAST_GROUP_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;

        MessageTypeA.Message.GroupMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.GroupMessage.class);
            LOG.warn("receive GroupMessage... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }


        if (msg != null) {

            List<String> idList = message.getNotifyIdListList();
            // 收到distribute推送，好友广播
            for (String id : idList) {
                MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder.buildGroupMessage(message.getFrom(), message.getTo(), msg);
                SpringContextHolder.getBean(MessageSender.class).sendToConnector(m, id);
            }

        }

        // 回复确认
        SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


    }
}

