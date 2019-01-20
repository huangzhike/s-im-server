package mmp.im.auth.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.AttributeKeyHolder;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;

public class ServerLoginHandler implements IMessageTypeHandler {


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
            channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).set(userId);


            ConnectionHolder.addServerConnection(userId, channelHandlerContext);

            System.out.println(msg.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
