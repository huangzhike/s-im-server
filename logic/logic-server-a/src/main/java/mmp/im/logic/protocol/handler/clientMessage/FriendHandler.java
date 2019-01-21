package mmp.im.logic.protocol.handler.clientMessage;

import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.logic.service.XService;
import mmp.im.logic.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendHandler implements IMQMessageTypeHandler {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.FRIEND_VALUE);
    }

    @Override
    public void process(Object object) {

        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;

        ClientMessageBody.ClientMessage.Friend msg = null;
        try {
            msg = message.getData().unpack(ClientMessageBody.ClientMessage.Friend.class);

        } catch (Exception e) {
            e.printStackTrace();
        }


        LOG.warn("FriendHandler process message -> {} ", message);

        SpringContextHolder.getBean(XService.class);

    }
}

