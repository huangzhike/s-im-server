package mmp.im.auth.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

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


            MessageSender.sendToServer(message);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

