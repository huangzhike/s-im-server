package mmp.im.logic.protocol.handler.clientMessage;

import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.logic.service.XService;
import mmp.im.logic.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupHandler implements IMQMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {
        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.GROUP_VALUE);
    }

    @Override
    public void process(Object object) {

        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        ClientMessageBody.ClientMessage.Group msg = null;
        try {
            msg = message.getData().unpack(ClientMessageBody.ClientMessage.Group.class);

        } catch (Exception e) {
            e.printStackTrace();
        }


        LOG.warn("GroupHandler process message -> {} ", message);

        SpringContextHolder.getBean(XService.class);

    }
}

