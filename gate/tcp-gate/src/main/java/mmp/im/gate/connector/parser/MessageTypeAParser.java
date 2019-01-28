package mmp.im.gate.connector.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.util.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.protocol.parser.AbstractProtocolParser;
import mmp.im.common.protocol.parser.IProtocolParser;

public class MessageTypeAParser extends AbstractProtocolParser implements IProtocolParser {

    public MessageTypeAParser() {

        this.initHandler("mmp.im.gate.connector.handler.messageTypeA", IMessageHandler.class);
    }

    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.PROTOBUF_MESSAGE.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        MessageTypeA.Message message = null;

        try {
            message = MessageTypeA.Message.parseFrom(bytes);

        } catch (InvalidProtocolBufferException e) {
            LOG.error("parseFrom Exception... {}", e);
        }

        if (message != null) {

            String type = String.valueOf(message.getType().getNumber());

            this.assignHandler(channelHandlerContext, type, message);

        }

    }

}
