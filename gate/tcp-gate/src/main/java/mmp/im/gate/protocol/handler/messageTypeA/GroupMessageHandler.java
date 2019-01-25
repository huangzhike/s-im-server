package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.gate.util.SpringContextHolder;


public class GroupMessageHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.GROUP_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;
        SpringContextHolder.getBean("", MessageSender.class).sendTo("message", null);

        MessageTypeA.Message.GroupMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.GroupMessage.class);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

    }
}

