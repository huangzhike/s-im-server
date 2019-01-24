package mmp.im.auth.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageTypeAParser implements IProtocolParser {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.auth.protocol.handler.messageTypeA", IMessageTypeHandler.class);

        classList.forEach(v -> {
            try {
                IMessageTypeHandler e = (IMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                LOG.error("MessageTypeAParser parseFrom Exception... {}", e);
            }
        });


    }


    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.MESSAGE.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        String channelId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();
        // 没登陆就关闭
        if (channelId == null) {
            // channelHandlerContext.channel().close();
            // return;
        }

        MessageTypeA.Message message = null;

        try {
            message = MessageTypeA.Message.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("parseFrom Exception... {}", e);
        }
        if (message != null) {


            String type = String.valueOf(message.getType().getNumber());

            IMessageTypeHandler handler = (IMessageTypeHandler) this.getMsgTypeHandlers().get(type);

            if (handler != null) {


                handler.process(channelHandlerContext, message);
            }


            // 回复确认

        }


    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}

