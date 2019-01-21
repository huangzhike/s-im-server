package mmp.im.logic.protocol.parser.clientMessage;

import com.google.protobuf.InvalidProtocolBufferException;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.common.protocol.parser.IMQProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientMessageParser implements IMQProtocolParser {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<String, IMQMessageTypeHandler> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.logic.protocol.handler.clientMessage", IMQMessageTypeHandler.class);

        classList.forEach(v -> {
            try {
                IMQMessageTypeHandler e = (IMQMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        LOG.warn("ClientMessageParser size -> {}", msgTypeHandlers.size());

    }


    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.MESSAGE.getType();
    }

    @Override
    public void parse(byte[] bytes) {

        ClientMessageBody.ClientMessage msg = null;

        try {
            msg = ClientMessageBody.ClientMessage.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        if (msg != null) {

            String type = String.valueOf(msg.getType().getNumber());

            IMQMessageTypeHandler handler = this.getMsgTypeHandlers().get(type);

            LOG.warn("ClientMessageParser parse -> {}", msg);

            if (handler != null) {
                handler.process(msg);
            }

        }

    }

    private Map<String, IMQMessageTypeHandler> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}
