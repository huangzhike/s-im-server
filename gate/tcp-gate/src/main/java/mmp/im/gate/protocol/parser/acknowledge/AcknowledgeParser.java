package mmp.im.gate.protocol.parser.acknowledge;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.protocol.handler.IMessageTypeHandler;
import mmp.im.gate.protocol.parser.IProtocolParser;
import mmp.im.gate.util.PackageUtil;
import mmp.im.protocol.AcknowledgeBody;
import mmp.im.protocol.ProtocolHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcknowledgeParser implements IProtocolParser {

    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol.handler.acknowledge", IMessageTypeHandler.class);

        classList.forEach(v -> {
            try {
                IMessageTypeHandler e = (IMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }


    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType();
    }

    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        AcknowledgeBody.Acknowledge acknowledge = null;

        try {
            acknowledge = AcknowledgeBody.Acknowledge.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (acknowledge != null) {
            String type = String.valueOf(ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType());
            IMessageTypeHandler handler = (IMessageTypeHandler) this.getMsgTypeHandlers().get(type);
            if (handler != null) {
                handler.process(channelHandlerContext, acknowledge);
            }
        }


    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}

