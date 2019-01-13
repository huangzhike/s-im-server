package mmp.im.gate.protocol.handler.clientLogin;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.cache.connection.ConnectionHolder;
import mmp.im.gate.config.AttributeKeyHolder;
import mmp.im.gate.protocol.handler.IMessageTypeHandler;
import mmp.im.protocol.ClientLoginBody.ClientLogin;
import mmp.im.protocol.ProtocolHeader;

import java.util.concurrent.atomic.AtomicLong;

public class LoginHandler implements IMessageTypeHandler {

    @Override
    public String getHandlerName() {

        return String.valueOf(ProtocolHeader.ProtocolType.CLIENT_LOGIN.getType());
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        ClientLogin login = (ClientLogin) object;
        try {

            System.out.println(login.getToken());

            // 从AUTH获取用户TOKEN对比

            channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).set(login.getUserId());
            channelHandlerContext.channel().attr(AttributeKeyHolder.SEQ).set(new AtomicLong());

            ConnectionHolder.addConnection(login.getUserId(), channelHandlerContext);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

