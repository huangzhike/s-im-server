package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.cache.connection.AcceptorChannelMap;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.ClientLogout;
import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

public class ClientLogoutHandler  extends CheckHandler implements INettyMessageHandler {


    private final String name = ClientLogout.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();

        if (!this.login(channel)) {
            LOG.warn("未登录");
        }



        ClientLogout message = (ClientLogout) object;

        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");
            return;
        }
        // 不需回复确认

        String userId = channel.attr(AttributeKeyHolder.CHANNEL_ID).get();


        // 移除并关闭
        SpringContextHolder.getBean(AcceptorChannelMap.class).removeChannel(userId);


        String serverId = ContextHolder.getServeId();

        // 生成消息待转发

        ClientStatus m = MessageBuilder.buildClientStatus(message.getUserId(), serverId, false, "");


        // distribute
        ContextHolder.getMessageSender().sendToAcceptor(m);
        // 发的消息待确认
        ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));



        ReferenceCountUtil.release(object);
    }
}

