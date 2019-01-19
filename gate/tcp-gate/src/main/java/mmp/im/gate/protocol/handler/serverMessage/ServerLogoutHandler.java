package mmp.im.gate.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.util.AttributeKeyHolder;
import mmp.im.protocol.ServerMessageBody;
import mmp.im.server.tcp.MessageSender;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;

public class ServerLogoutHandler  implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ServerMessageBody.ServerMessage.MsgType.SERVER_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ServerMessageBody.ServerMessage message = (ServerMessageBody.ServerMessage) object;
        try {
            ServerMessageBody.ServerMessage.ServerLogout msg = message.getData().unpack(ServerMessageBody.ServerMessage.ServerLogout.class);
            System.out.println(msg.getName());

            String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).get();

            if (userId != null ) {
                // 直接关闭连接，不发送确认了
                ConnectionHolder.removeServerConnection(userId);
                channelHandlerContext.channel().close();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
