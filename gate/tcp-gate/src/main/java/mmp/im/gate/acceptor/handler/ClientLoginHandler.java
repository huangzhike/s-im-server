package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.util.token.JWTUtil;
import mmp.im.gate.util.ContextHolder;
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
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        Channel channel = channelHandlerContext.channel();

        String userId = (String) JWTUtil.parseJWT(message.getToken()).get("id");

        LOG.warn("用户登录... {}", userId);

        // 已登录
        if (this.login(channel)) {
            ReferenceCountUtil.release(object);
            LOG.warn("已登录");
            return;
        }

        channel.attr(AttributeKeyHolder.CHANNEL_ID).set(userId);
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).set(new ArrayList<>());

        // 说明是重复发送，不处理，只回复ACK
        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");
            ReferenceCountUtil.release(object);
            return;
        }

        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());

        // 添加进channel map
        ContextHolder.getAcceptorChannelMap().addChannel(userId, channelHandlerContext);

        // 生成消息待转发
        ClientStatus m = MessageBuilder.buildClientStatus(userId, ContextHolder.getServeId(), true, message.getClientInfo());
        // distribute
        ContextHolder.getMessageSender().sendToAcceptor(m);
        // 发的消息待确认
        ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));

        ReferenceCountUtil.release(object);
    }


}

