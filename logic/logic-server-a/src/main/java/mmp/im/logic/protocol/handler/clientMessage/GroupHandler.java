package mmp.im.logic.protocol.handler.clientMessage;

import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;


public class GroupHandler implements IMQMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.GROUP_VALUE);
    }

    @Override
    public void process(Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Group msg = message.getData().unpack(ClientMessageBody.ClientMessage.Group.class);
            System.out.println(msg.getName());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

