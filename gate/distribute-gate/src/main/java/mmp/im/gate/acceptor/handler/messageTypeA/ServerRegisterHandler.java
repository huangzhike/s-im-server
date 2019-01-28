package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.util.SpringContextHolder;

public class ServerRegisterHandler extends MessageTypeAHandler implements IMessageHandler {


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.SERVER_REGISTER_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;

        MessageTypeA.Message.ServerRegister msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ServerRegister.class);

            LOG.warn("receive ServerRegister... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        String id = channelHandlerContext.channel().remoteAddress().toString();


        if (msg != null) {
            String serverId = msg.getId();
            SpringContextHolder.getBean(AcceptorChannelHandlerMap.class).addChannel(serverId, channelHandlerContext);

            channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(serverId);

        }

        // 回复确认
        SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


    }
}


