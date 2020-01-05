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

        // 已读消息
        ReadMessage message = (ReadMessage) object;

        LOG.warn("ReadMessage... {}", message);

        if (message.getType().equals(ReadMessage.Type.FRIEND)) {
            // 私聊
            ContextHolder.getFriendMessageService().updateReadUserFriendMessageId(message.getFrom(), message.getTo(), message.getSeqId());

        } else if (message.getType().equals(ReadMessage.Type.GROUP)) {
            // 群聊
            ContextHolder.getGroupMessageService().updateReadUserGroupMessageId(message.getFrom(), message.getTo(), message.getSeqId());

        }

    }
}
