package mmp.im.gate.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;

public class UserLoginHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ServerMessageBody.ServerMessage.MsgType.USER_LOGIN_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channel, Object object) {


        ServerMessageBody.ServerMessage message = (ServerMessageBody.ServerMessage) object;
        try {
            ServerMessageBody.ServerMessage.UserLogin msg = message.getData().unpack(ServerMessageBody.ServerMessage.UserLogin.class);
            System.out.println(msg.getName());

            // todo 收到别的端用户登陆信息，保存给用户显示在线状态

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

