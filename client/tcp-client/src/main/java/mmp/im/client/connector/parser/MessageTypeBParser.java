package mmp.im.client.connector.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeB;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.AbstractProtocolParser;
import mmp.im.common.protocol.parser.IProtocolParser;

public class MessageTypeBParser extends AbstractProtocolParser implements IProtocolParser {

    public MessageTypeBParser() {

        this.initHandler("mmp.im.client.connector.handler.messageTypeB", IMessageTypeHandler.class);
    }

    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.PROTOBUF_ACKNOWLEDGE.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        MessageTypeB.Acknowledge message = null;

        try {
            message = MessageTypeB.Acknowledge.parseFrom(bytes);

        } catch (InvalidProtocolBufferException e) {
            LOG.error("parseFrom Exception... {}", e);
        }

        if (message != null) {

            String type = String.valueOf(ProtocolHeader.ProtocolType.PROTOBUF_ACKNOWLEDGE.getType());

            this.assignHandler(channelHandlerContext, type, message);

        }

    }

}
