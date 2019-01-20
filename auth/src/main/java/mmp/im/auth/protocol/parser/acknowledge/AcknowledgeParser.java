package mmp.im.auth.protocol.parser.acknowledge;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.AcknowledgeBody;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.server.tcp.AttributeKeyHolder;
import mmp.im.common.util.reflect.PackageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcknowledgeParser implements IProtocolParser {

    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.auth.protocol.handler.acknowledge", IMessageTypeHandler.class);

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


        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).get();
        // 没登陆就关闭
        if (userId == null) {
            channelHandlerContext.channel().close();
            return;
        }


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

