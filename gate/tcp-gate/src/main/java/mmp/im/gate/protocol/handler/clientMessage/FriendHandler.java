package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.protocol.AcknowledgeBody;
import mmp.im.protocol.ClientMessageBody;
import mmp.im.server.tcp.MessageSender;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;

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
            MessageSender.sendToClient(message.getTo(), message);
            MessageSender.sendToServer( message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

