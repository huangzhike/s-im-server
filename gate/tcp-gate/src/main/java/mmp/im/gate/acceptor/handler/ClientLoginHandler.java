package mmp.im.gate.acceptor.handler;

import io.netty.channel.ChannelHandlerContext; 
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.acceptor.ClientToGateAcceptor;
import mmp.im.gate.service.AuthService;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


import static mmp.im.common.protocol.ProtobufMessage.ClientLogin;


public class ClientLoginHandler  implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientLogin.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        ClientLogin message = (ClientLogin) object;


        // 从AUTH获取用户TOKEN对比
        SpringContextHolder.getBean(AuthService.class);

        String userId = message.getId();

        if (userId != null) {

            channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(userId);

            // 如果contains seq，说明是重复发送，不处理，只回复ACK
            channelHandlerContext.channel().attr(AttributeKeyHolder.REV_SEQ_LIST).set(new ArrayList<>());

            // 添加进channel map
            SpringContextHolder.getBean(AcceptorChannelHandlerMap.class).addChannel(userId, channelHandlerContext);

            String serverId = SpringContextHolder.getBean(ClientToGateAcceptor.class).getServeId();
            // 生成消息
            // Message m = (Message) MessageBuilder.buildClientStatus(message.getFrom(), message.getTo(), userId, serverId, true);

            // distribute
            // SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);

            // 回复确认
            // SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


        }


    }
}

