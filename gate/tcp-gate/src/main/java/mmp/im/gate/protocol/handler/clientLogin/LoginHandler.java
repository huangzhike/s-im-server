package mmp.im.gate.protocol.handler.clientLogin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.util.http.HTTPUtil;
import mmp.im.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.gate.config.AttributeKeyHolder;
import mmp.im.server.tcp.protocol.handler.IMessageTypeHandler;
import mmp.im.protocol.ClientLoginBody.ClientLogin;
import mmp.im.protocol.ProtocolHeader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
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
            channelHandlerContext.channel().attr(AttributeKeyHolder.SEQ).set(new AtomicLong());

            ConnectionHolder.addClientConnection(login.getUserId(), channelHandlerContext);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

