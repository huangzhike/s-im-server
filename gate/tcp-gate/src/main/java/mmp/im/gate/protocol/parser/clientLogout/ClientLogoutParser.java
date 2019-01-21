package mmp.im.gate.protocol.parser.clientLogout;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ClientLogoutBody;
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

public class ClientLogoutParser implements IProtocolParser {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol.handler.clientLogout", IMessageTypeHandler.class);

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
        return ProtocolHeader.ProtocolType.CLIENT_LOGOUT.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {


        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();
        // 没登陆就关闭
        if (userId == null) {
            channelHandlerContext.channel().close();
            return;
        }

        ClientLogoutBody.ClientLogout logout = null;

        try {
            logout = ClientLogoutBody.ClientLogout.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (logout != null) {
            String type = String.valueOf(ProtocolHeader.ProtocolType.CLIENT_LOGOUT.getType());
            IMessageTypeHandler handler = (IMessageTypeHandler) this.getMsgTypeHandlers().get(type);
            if (handler != null) {
                handler.process(channelHandlerContext, logout);
            }
        }


    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}


