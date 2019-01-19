package mmp.im.gate.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.util.AttributeKeyHolder;
import mmp.im.protocol.ClientMessageBody;
import mmp.im.protocol.ServerMessageBody;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;

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
