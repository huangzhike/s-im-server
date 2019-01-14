package mmp.im.gate.protocol.parser.clientLogin;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;
import mmp.im.server.tcp.protocol.parser.IProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import mmp.im.protocol.ClientLoginBody;
import mmp.im.protocol.ProtocolHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientLoginParser implements IProtocolParser {

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
            }
        }


    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}


