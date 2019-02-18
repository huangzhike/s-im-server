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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;


public class GroupMessageHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final String name = GroupMessage.getDefaultInstance().getClass().toString();

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

        GroupMessage message = (GroupMessage) object;
        LOG.warn("GroupMessage... {}", message);
        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");
            // release
            return;
        }
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());

        GroupMessage m = MessageBuilder.buildTransGroupMessage(message);
        // 转发到中心server
        ContextHolder.getMessageSender().sendToAcceptor(m);
        // 发的消息待确认
        ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));
        ReferenceCountUtil.release(object);
    }
}

