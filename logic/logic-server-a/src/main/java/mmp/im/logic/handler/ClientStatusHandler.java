package mmp.im.logic.handler;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.handler.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

public class ClientStatusHandler implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientStatus.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(MessageLite object) {

        ClientStatus message = (ClientStatus) object;

        LOG.warn("ClientStatus... {}", message);

        // 保存用户登录记录

    }
}


