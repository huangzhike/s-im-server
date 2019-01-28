package mmp.im.gate.connector.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeD;
import mmp.im.common.protocol.util.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.protocol.parser.AbstractProtocolParser;
import mmp.im.common.protocol.parser.IProtocolParser;

public class MessageTypeDParser extends AbstractProtocolParser implements IProtocolParser {

    public MessageTypeDParser() {

        this.initHandler("mmp.im.gate.connector.handler.messageTypeD", IMessageHandler.class);
    }

    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.PROTOBUF_MESSAGE_LITE.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        MessageTypeD.MessageLite message = null;

        try {
            message = MessageTypeD.MessageLite.parseFrom(bytes);

        } catch (InvalidProtocolBufferException e) {
            LOG.error("parseFrom Exception... {}", e);
        }

        if (message != null) {

            String type = String.valueOf(ProtocolHeader.ProtocolType.PROTOBUF_MESSAGE_LITE.getType());

            this.assignHandler(channelHandlerContext, type, message);

        }

    }

}

