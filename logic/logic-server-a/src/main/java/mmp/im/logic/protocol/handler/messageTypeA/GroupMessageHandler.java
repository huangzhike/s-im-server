package mmp.im.logic.protocol.handler.messageTypeA;

import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.logic.service.XService;
import mmp.im.logic.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupMessageHandler implements IMQMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.GROUP_MESSAGE_VALUE);
    }

    @Override
    public void process(Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;

        MessageTypeA.Message.GroupMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.GroupMessage.class);
        } catch (Exception e) {
            LOG.error("GroupMessageHandler parsers Exception... {}", e);
        }

        LOG.warn("GroupMessageHandler process message... {}", message);

        SpringContextHolder.getBean(XService.class);

    }
}

