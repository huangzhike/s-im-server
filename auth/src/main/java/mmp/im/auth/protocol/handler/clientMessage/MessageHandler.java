package mmp.im.auth.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;

public class MessageHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.Msg_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Message msg = message.getData().unpack(ClientMessageBody.ClientMessage.Message.class);
            System.out.println(msg.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
