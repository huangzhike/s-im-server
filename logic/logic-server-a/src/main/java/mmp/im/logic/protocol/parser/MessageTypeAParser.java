package mmp.im.logic.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.common.protocol.parser.IMQProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageTypeAParser implements IMQProtocolParser {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<String, IMQMessageTypeHandler> msgTypeHandlers = new HashMap<>();

    {

        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.logic.protocol.handler.messageTypeA", IMQMessageTypeHandler.class);

        classList.forEach(v -> {
            try {
                IMQMessageTypeHandler e = (IMQMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                LOG.error("init MessageTypeAParser Exception... {}", e);
            }
        });

        LOG.warn("init MessageTypeAParser size... {}", msgTypeHandlers.size());

    }


    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.MESSAGE.getType();
    }

    @Override
    public void parse(byte[] bytes) {

        MessageTypeA.Message message = null;

        try {
            message = MessageTypeA.Message.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("MessageTypeAParser parse Exception... {}", e);
        }

        if (message != null) {

            LOG.warn("MessageTypeAParser parsing message... {}", message);

            String type = String.valueOf(message.getType().getNumber());

            IMQMessageTypeHandler handler = this.getMsgTypeHandlers().get(type);

            if (handler != null) {

                handler.process(message);

                LOG.warn("MessageTypeAParser handler processing message... {}", message);
            }

        }

    }

    private Map<String, IMQMessageTypeHandler> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}
