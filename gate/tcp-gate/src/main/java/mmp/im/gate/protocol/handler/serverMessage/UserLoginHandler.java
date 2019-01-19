package mmp.im.gate.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.protocol.ServerMessageBody;
import mmp.im.server.tcp.MessageSender;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;

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


                MessageSender.sendToServer(  message);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

