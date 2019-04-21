package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.token.JWTUtil;
import mmp.im.gate.acceptor.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static mmp.im.common.protocol.ProtobufMessage.ClientLogin;
import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;


public class ClientLoginHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientLogin.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        ClientLogin message = (ClientLogin) object;

        LOG.warn("ClientLogin... {}", message);
        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        Channel channel = channelHandlerContext.channel();

        String userId = (String) JWTUtil.parseJWT(message.getToken()).get("id");

        LOG.warn("用户登录... {}", userId);

        // 已登录
        if (this.login(channel)) {

            LOG.warn("已登录");
            return;
        }

        channel.attr(AttributeKeyHolder.CHANNEL_ID).set(userId);
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).set(new ArrayList<>());

        // 说明是重复发送，不处理，只回复ACK
        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");

            return;
        }

        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());

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

