package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.gate.service.AuthService;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLoginHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGIN_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageSender.sendToServers(message);
        MessageTypeA.Message.ClientLogin msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ClientLogin.class);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        if (msg == null) {
            return;
        }

        /*
         * HTTP调用，考虑RPC是否好一点？
         * */
        SpringContextHolder.getBean(AuthService.class);

        // 从AUTH获取用户TOKEN对比

        channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(msg.getId());

        ConnectionHolder.addClientConnection(msg.getId(), channelHandlerContext);

    }
}

