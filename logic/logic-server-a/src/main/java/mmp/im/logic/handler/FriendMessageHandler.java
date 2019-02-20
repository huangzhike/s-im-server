package mmp.im.logic.handler;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.logic.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;

public class FriendMessageHandler implements IMessageHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final String name = FriendMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(MessageLite object) {


        FriendMessage message = (FriendMessage) object;

        LOG.warn("FriendMessage... {}", message);

        ContextHolder.getFriendMessageService().saveFriendMessage(message);


        ContextHolder.getSessionService().addRecentFriendSession(message.getFrom(),message.getTo());
    }
}

