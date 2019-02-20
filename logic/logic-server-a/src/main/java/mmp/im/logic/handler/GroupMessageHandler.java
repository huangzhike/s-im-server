package mmp.im.logic.handler;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.logic.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

public class GroupMessageHandler implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final String name = GroupMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(MessageLite object) {


        GroupMessage message = (GroupMessage) object;

        LOG.warn("GroupMessage... {}", message);

        ContextHolder.getGroupMessageService().saveGroupMessage(message);

        ContextHolder.getSessionService().addRecentGroupSession(message.getFrom(),message.getTo());
    }
}

