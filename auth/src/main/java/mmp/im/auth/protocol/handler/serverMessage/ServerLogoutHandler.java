package mmp.im.auth.protocol.handler.serverMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogoutHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(ServerMessageBody.ServerMessage.MsgType.SERVER_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ServerMessageBody.ServerMessage message = (ServerMessageBody.ServerMessage) object;
        try {
            ServerMessageBody.ServerMessage.ServerLogout msg = message.getData().unpack(ServerMessageBody.ServerMessage.ServerLogout.class);
            System.out.println(msg.getName());

            String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();

            if (userId != null) {
                // 直接关闭连接，不发送确认了
                ConnectionHolder.removeServerConnection(userId);
                channelHandlerContext.channel().close();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
