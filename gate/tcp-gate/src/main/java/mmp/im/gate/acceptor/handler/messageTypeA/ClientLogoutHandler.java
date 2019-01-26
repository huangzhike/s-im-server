package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.util.MessageBuilder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.gate.acceptor.ClientToGateAcceptor;
import mmp.im.gate.util.SpringContextHolder;

public class ClientLogoutHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageTypeA.Message message = (MessageTypeA.Message) object;


        MessageTypeA.Message.ClientLogout msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ClientLogout.class);
            LOG.warn("receive ClientLogout... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();


        String serverId = SpringContextHolder.getBean(ClientToGateAcceptor.class).getServeId();

        MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder.buildClientStatus(message.getFrom(), message.getTo(), userId, serverId, false);

        // distribute
        SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);


        // 回复确认
        SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


        // 移除并关闭
        SpringContextHolder.getBean(AcceptorChannelHandlerMap.class).removeChannel(userId);


    }
}

