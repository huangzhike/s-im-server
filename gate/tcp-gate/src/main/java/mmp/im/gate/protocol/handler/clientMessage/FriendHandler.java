package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.MessageSender;

public class FriendHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.FRIEND_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Friend msg = message.getData().unpack(ClientMessageBody.ClientMessage.Friend.class);
            System.out.println(msg.getName());
            // 单聊消息
            MessageSender.sendToClient(message.getTo(), message);
            // 其他端有登陆
            MessageSender.sendToServer(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

