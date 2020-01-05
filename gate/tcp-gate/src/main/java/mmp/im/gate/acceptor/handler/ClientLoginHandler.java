package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.token.JWTUtil;
import mmp.im.gate.connector.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static mmp.im.common.protocol.ProtobufMessage.ClientLogin;
import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;


public class ClientLoginHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientLogin.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        ClientLogin message = (ClientLogin) object;

        LOG.warn("ClientLogin {}", message);
        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        Channel channel = channelHandlerContext.channel();

        String userId = (String) JWTUtil.parseJWT(message.getToken()).get("id");

        LOG.warn("login {}", userId);

        String uId = channel.attr(AttributeKeyConstant.CHANNEL_ID).get();
        if (uId != null) {
            LOG.warn("already login");
            return;
        }

        channel.attr(AttributeKeyConstant.CHANNEL_ID).set(userId);

        Map<Long, Long> receivedCache = channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).get();

        if (receivedCache.containsKey(message.getSeq())) {
            LOG.warn("repeat");
            return;
        }

        // 加入已收到的消息
        receivedCache.putIfAbsent(message.getSeq(), message.getSeq());

        // 添加进channel map
        AcceptorChannelManager.getInstance().addChannel(userId, channelHandlerContext);

        // 生成消息待转发
        ClientStatus m = MessageBuilder.buildClientStatus(userId, Config.SERVER_ID, true, message.getClientInfo());
        // distribute
        MessageSender.sendToAcceptor(m);
        // 发的消息待确认
        ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);


    }


}

