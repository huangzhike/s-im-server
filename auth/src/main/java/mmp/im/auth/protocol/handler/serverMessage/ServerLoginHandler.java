package mmp.im.auth.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLoginHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(ServerMessageBody.ServerMessage.MsgType.SERVER_LOGIN_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ServerMessageBody.ServerMessage message = (ServerMessageBody.ServerMessage) object;
        try {
            ServerMessageBody.ServerMessage.ServerLogin msg = message.getData().unpack(ServerMessageBody.ServerMessage.ServerLogin.class);

            // 从AUTH获取用户TOKEN对比

            String userId = channelHandlerContext.channel().remoteAddress().toString();
            channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(userId);


            ConnectionHolder.addServerConnection(userId, channelHandlerContext);

            System.out.println(msg.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
