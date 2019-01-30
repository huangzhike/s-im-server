package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;


public class FriendMessageHandler  extends CheckHandler implements INettyMessageHandler {

    private final String name = FriendMessage.getDefaultInstance().getClass().toString();

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

        FriendMessage message = (FriendMessage) object;

        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");
            return;
        }

        FriendMessage m = MessageBuilder.buildTransFriendMessage(message);
        // 转发单聊消息
        ContextHolder.getMessageSender().sendToAcceptor(m);
        // 发的消息待确认
        ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));

        ReferenceCountUtil.release(object);
    }
}
