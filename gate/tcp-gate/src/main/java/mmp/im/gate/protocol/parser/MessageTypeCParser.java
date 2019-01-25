package mmp.im.gate.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeC;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.AbstractProtocolParser;
import mmp.im.common.protocol.parser.IProtocolParser;

public class MessageTypeCParser extends AbstractProtocolParser implements IProtocolParser {

    public MessageTypeCParser() {

        this.initHandler("mmp.im.gate.protocol.handler.messageTypeC", IMessageTypeHandler.class);
    }

    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.PROTOBUF_HEARTBEAT.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        MessageTypeC.Heartbeat message = null;

        try {
            message = MessageTypeC.Heartbeat.parseFrom(bytes);

        } catch (InvalidProtocolBufferException e) {
            LOG.error("parseFrom Exception... {}", e);
        }

        if (message != null) {

            String type = String.valueOf(ProtocolHeader.ProtocolType.PROTOBUF_HEARTBEAT.getType());

            this.assignHandler(channelHandlerContext, type, message);

        }

    }

}

