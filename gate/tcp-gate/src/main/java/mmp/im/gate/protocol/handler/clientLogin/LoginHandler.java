package mmp.im.gate.protocol.handler.clientLogin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ClientLoginBody.ClientLogin;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.AttributeKeyHolder;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.util.http.HTTPUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

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


            HTTPUtil.get("", null, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        // Object object = JSON.parseObject(response.body().string(), Object.class);
                        Object object = JSON.parseObject(response.body().string(), new TypeReference<Object>() {
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            // 从AUTH获取用户TOKEN对比

            channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID).set(login.getUserId());


            ConnectionHolder.addClientConnection(login.getUserId(), channelHandlerContext);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

