package mmp.im.logic.protocol.handler.messageTypeA;

import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.logic.service.XService;
import mmp.im.logic.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendMessageHandler implements IMQMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.FRIEND_MESSAGE_VALUE);
    }

    @Override
    public void process(Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;

        MessageTypeA.Message.FriendMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.FriendMessage.class);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        LOG.warn("processing message... {}", message);

        SpringContextHolder.getBean(XService.class);

    }
}

