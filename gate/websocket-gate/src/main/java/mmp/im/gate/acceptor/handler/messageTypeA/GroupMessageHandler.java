package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.util.SpringContextHolder;


public class GroupMessageHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.GROUP_MESSAGE_VALUE);
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

        MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder.buildGroupMessage(message.getFrom(), message.getTo(), msg);
        // 转发到中心server
        SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);


    }
}

