package mmp.im.gate.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;

public class UserLogoutHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ServerMessageBody.ServerMessage.MsgType.USER_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channel, Object object) {


        ServerMessageBody.ServerMessage message = (ServerMessageBody.ServerMessage) object;
        try {
            ServerMessageBody.ServerMessage.UserLogout msg = message.getData().unpack(ServerMessageBody.ServerMessage.UserLogout.class);
            System.out.println(msg.getName());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

