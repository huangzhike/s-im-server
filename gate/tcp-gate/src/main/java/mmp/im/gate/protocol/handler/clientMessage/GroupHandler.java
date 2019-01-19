package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.model.User;
import mmp.im.protocol.AcknowledgeBody;
import mmp.im.protocol.ClientMessageBody;
import mmp.im.server.tcp.MessageSender;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;

import java.util.List;


public class GroupHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.GROUP_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Group msg = message.getData().unpack(ClientMessageBody.ClientMessage.Group.class);
            System.out.println(msg.getName());

            List<User> userList = null;
            if (userList != null) {
                userList.forEach(user -> {
                    MessageSender.sendToClient(user.getId(), message);
                    MessageSender.sendToServer(  message);
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

