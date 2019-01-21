package mmp.im.gate.protocol.handler.clientLogout;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ClientLogoutBody.ClientLogout;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {
        return String.valueOf(ProtocolHeader.ProtocolType.CLIENT_LOGOUT.getType());
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        ClientLogout logout = (ClientLogout) object;
        try {

            System.out.println(logout.getToken());


            // 从AUTH获取用户TOKEN对比

            String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();

            if (userId != null && userId.equals(logout.getUserId())) {
                // 直接关闭连接，不发送确认了
                ConnectionHolder.removeClientConnection(userId);
                channelHandlerContext.channel().close();
                // 发给Auth
                MessageSender.sendToServer(logout);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

