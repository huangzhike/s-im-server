package mmp.im.logic.handler;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.logic.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.ReadMessage;

public class ReadMessageHandler implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final String name = ReadMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(MessageLite object) {


        ReadMessage message = (ReadMessage) object;

        LOG.warn("ReadMessage... {}", message);

        ContextHolder.getGroupMessageService();

        ContextHolder.getFriendMessageService();
    }
}
