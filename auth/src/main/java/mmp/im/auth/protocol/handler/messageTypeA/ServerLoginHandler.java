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
        try {
            MessageTypeA.Message.ServerLogin msg = message.getData().unpack(MessageTypeA.Message.ServerLogin.class);

            // 从AUTH获取用户TOKEN对比

            String userId = channelHandlerContext.channel().remoteAddress().toString();
            channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(userId);


            ConnectionHolder.addServerConnection(userId, channelHandlerContext);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
