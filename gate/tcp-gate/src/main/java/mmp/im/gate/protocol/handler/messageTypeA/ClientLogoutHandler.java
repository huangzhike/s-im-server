package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLogoutHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageTypeA.Message message = (MessageTypeA.Message) object;

        // 从AUTH获取用户TOKEN对比

        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();

        // 直接关闭连接，不发送确认了
        ConnectionHolder.removeClientConnection(userId);
        // channelHandlerContext.channel().close();
        // 发给Auth
        MessageSender.sendToServers(message);


    }
}

