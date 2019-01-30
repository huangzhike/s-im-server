package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;

import java.util.ArrayList;

import static mmp.im.common.protocol.ProtobufMessage.ClientLogin;
import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;


public class ClientLoginHandler extends CheckHandler implements INettyMessageHandler {


    private final String name = ClientLogin.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        ClientLogin message = (ClientLogin) object;
        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        Channel channel = channelHandlerContext.channel();
        // TODO 从AUTH获取用户TOKEN对比

        String userId = message.getUserId();

        // 已登录
        if (this.login(channel)) {
            return;
        }

        channel.attr(AttributeKeyHolder.CHANNEL_ID).set(userId);
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).set(new ArrayList<>());

        // 说明是重复发送，不处理，只回复ACK
        if (this.duplicate(channel, message.getSeq())) {
            return;
        }

        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());

        // 添加进channel map
        ContextHolder.getAcceptorChannelMap().addChannel(userId, channelHandlerContext);

        String serverId = ContextHolder.getServeId();
        // 生成消息待转发
        ClientStatus m = MessageBuilder.buildClientStatus(userId, serverId, true, "");
        // distribute
        ContextHolder.getMessageSender().sendToAcceptor(m);
        // 发的消息待确认
        ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));

        ReferenceCountUtil.release(object);
    }





}
