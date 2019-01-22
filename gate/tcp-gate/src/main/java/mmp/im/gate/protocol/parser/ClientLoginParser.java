package mmp.im.gate.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.AcknowledgeBody;
import mmp.im.common.protocol.ClientLoginBody;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientLoginParser implements IProtocolParser {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol.handler.clientLogin", IMessageTypeHandler.class);

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
        return ProtocolHeader.ProtocolType.CLIENT_LOGIN.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {


        ClientLoginBody.ClientLogin login = null;

        try {
            login = ClientLoginBody.ClientLogin.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (login != null) {
            String type = String.valueOf(ProtocolHeader.ProtocolType.CLIENT_LOGIN.getType());
            IMessageTypeHandler handler = (IMessageTypeHandler) this.getMsgTypeHandlers().get(type);
            if (handler != null) {
                handler.process(channelHandlerContext, login);

                // 回复确认
                AcknowledgeBody.Acknowledge.Builder builder = AcknowledgeBody.Acknowledge.newBuilder();

                channelHandlerContext.channel().writeAndFlush(builder.setAck(login.getSeq()).build());
            }
        }


    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}


