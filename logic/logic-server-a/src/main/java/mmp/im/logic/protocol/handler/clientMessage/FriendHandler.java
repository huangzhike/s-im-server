package mmp.im.logic.protocol.handler.clientMessage;

import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.logic.service.XService;
import mmp.im.logic.util.SpringContextHolder;

public class FriendHandler implements IMQMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.FRIEND_VALUE);
    }

    @Override
    public void process(Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Friend msg = message.getData().unpack(ClientMessageBody.ClientMessage.Friend.class);
            System.out.println(msg.getName());

            SpringContextHolder.getBean(XService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

