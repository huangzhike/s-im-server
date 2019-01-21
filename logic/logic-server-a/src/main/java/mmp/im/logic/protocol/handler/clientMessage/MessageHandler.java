package mmp.im.logic.protocol.handler.clientMessage;

import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler implements IMQMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.Msg_VALUE);
    }

    @Override
    public void process(Object object) {

        LOG.warn("MessageHandler -> {} ", object);

    }
}
