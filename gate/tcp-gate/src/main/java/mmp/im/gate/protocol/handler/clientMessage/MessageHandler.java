package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.protocol.AcknowledgeBody;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;
import mmp.im.protocol.ClientMessageBody;

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
