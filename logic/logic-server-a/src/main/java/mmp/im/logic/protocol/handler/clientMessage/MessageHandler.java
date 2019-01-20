package mmp.im.logic.protocol.handler.clientMessage;

import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;

public class MessageHandler implements IMQMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.Msg_VALUE);
    }

    @Override
    public void process(Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Message msg = message.getData().unpack(ClientMessageBody.ClientMessage.Message.class);
            System.out.println(msg.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
