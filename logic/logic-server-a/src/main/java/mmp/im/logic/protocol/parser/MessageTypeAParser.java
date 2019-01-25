package mmp.im.logic.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.common.protocol.parser.AbstractMQProtocolParser;
import mmp.im.common.protocol.parser.IMQProtocolParser;

public class MessageTypeAParser extends AbstractMQProtocolParser implements IMQProtocolParser {


    public MessageTypeAParser() {

        this.initHandler("mmp.im.logic.protocol.handler.messageTypeA", IMQMessageTypeHandler.class);
    }


    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.PROTOBUF_MESSAGE.getType();
    }

    @Override
    public void parse(byte[] bytes) {

        MessageTypeA.Message message = null;

        try {
            message = MessageTypeA.Message.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("parse Exception... {}", e);
        }

        if (message != null) {

            LOG.warn("message... {}", message);

            String type = String.valueOf(message.getType().getNumber());

            this.assignHandler(type, message);

        }

    }

}
