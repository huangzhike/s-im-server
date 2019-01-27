package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.acceptor.ClientToGateAcceptor;
import mmp.im.gate.service.AuthService;
import mmp.im.gate.util.SpringContextHolder;


public class ClientLoginHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGIN_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;

        MessageTypeA.Message.ClientLogin msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ClientLogin.class);

            LOG.warn("receive ClientLogin... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }


        if (msg != null) {
            // 从AUTH获取用户TOKEN对比
            SpringContextHolder.getBean(AuthService.class);

            String userId = msg.getId();

            if (userId != null) {

                channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(userId);

                // 添加进channel map
                SpringContextHolder.getBean(AcceptorChannelHandlerMap.class).addChannel(userId, channelHandlerContext);

                String serverId = SpringContextHolder.getBean(ClientToGateAcceptor.class).getServeId();
                // 生成消息
                MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder.buildClientStatus(message.getFrom(), message.getTo(), userId, serverId, true);

                // distribute
                SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);

                // 回复确认
                SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


            }

        }


    }
}

