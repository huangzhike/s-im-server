package mmp.im.auth.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogoutHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.SERVER_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageTypeA.Message.ServerLogout msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ServerLogout.class);

        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        String channelId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();

        if (channelId != null) {
            // 直接关闭连接，不发送确认了
            ConnectionHolder.removeServerConnection(channelId);
            // channelHandlerContext.channel().close();

        }

    }
}
