package mmp.im.gate.protocol.handler.messageTypeA;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.ConnectionHolder;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.common.util.http.HTTPUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientLoginHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGIN_STATUS);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageSender.sendToServers(message);
        MessageTypeA.Message.ClientLogin msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ClientLogin.class);

        } catch (Exception e) {
            e.printStackTrace();
        }


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

        channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(msg.getId());


        ConnectionHolder.addClientConnection(msg.getId(), channelHandlerContext);

    }
}

