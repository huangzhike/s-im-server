package mmp.im.gate.protocol.handler.clientLogout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.util.http.HTTPUtil;
import mmp.im.gate.util.AttributeKeyHolder;
import mmp.im.protocol.ClientLogoutBody.ClientLogout;
import mmp.im.protocol.ProtocolHeader;
import mmp.im.server.tcp.MessageSender;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class LogoutHandler implements IMessageTypeHandler {

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

            String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).get();

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

