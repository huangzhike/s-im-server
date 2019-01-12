package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.cache.connection.ConnectionHolder;
import mmp.im.gate.config.AttributeKeyHolder;
import mmp.im.gate.protocol.handler.IMessageTypeHandler;
import mmp.im.protocol.ClientMessageBody.ClientMessage;

public class LoginHandler implements IMessageTypeHandler {

    public String getHandlerName() {

        return String.valueOf(ClientMessage.MessageType.LOGIN_VALUE);
    }

    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        ClientMessage message = (ClientMessage) object;
        try {
            ClientMessage.Login login = message.getData().unpack(ClientMessage.Login.class);
            System.out.println(login.getToken());

            // 从AUTH获取用户TOKEN对比

            channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).set(login.getUserId());


            ConnectionHolder.addConnection(login.getUserId(), channelHandlerContext);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

