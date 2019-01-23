package mmp.im.gate.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeB;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageTypeBParser implements IProtocolParser {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol.handler.messageTypeB", IMessageTypeHandler.class);

        classList.forEach(v -> {
            try {
                IMessageTypeHandler e = (IMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }


    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        MessageTypeB.Acknowledge message = null;

        try {
            message = MessageTypeB.Acknowledge.parseFrom(bytes);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (message != null) {

            IMessageTypeHandler handler = (IMessageTypeHandler) this.getMsgTypeHandlers().get(ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType());
            if (handler != null) {
                handler.process(channelHandlerContext, message);
            }

        }

    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }

}
