package mmp.im.gate.acceptor.handler;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.acceptor.ClientToGateAcceptor;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.ClientLogout;

public class ClientLogoutHandler  implements IMessageHandler {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientLogout.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        ClientLogout message = (ClientLogout) object;

        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();


        String serverId = SpringContextHolder.getBean(ClientToGateAcceptor.class).getServeId();

        // Message m = (Message) MessageBuilder.buildClientStatus(message.getFrom(), message.getTo(), userId, serverId, false);

        // distribute
        // SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);


        // 回复确认
        // SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


        // 移除并关闭
        SpringContextHolder.getBean(AcceptorChannelHandlerMap.class).removeChannel(userId);

    }
}

