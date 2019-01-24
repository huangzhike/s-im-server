package mmp.im.auth.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLoginHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.SERVER_LOGIN_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageTypeA.Message.ServerLogin msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ServerLogin.class);

        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        // 从AUTH获取用户TOKEN对比

        String channelId = channelHandlerContext.channel().remoteAddress().toString();
        channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(channelId);


        ConnectionHolder.addServerConnection(channelId, channelHandlerContext);

    }
}
