package mmp.im.gate.connector.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

public class BroadcastGroupMessageHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = GroupMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();
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

        List<Long> idList = message.getBroadcastIdListList();

        if (idList == null) {
            return;
        }
        // 收到distribute推送，好友广播
        for (Long id : idList) {
            GroupMessage m = MessageBuilder.buildTransGroupMessage(message);
            ContextHolder.getMessageSender().sendToConnector(m, id);
            // 发的消息待确认
            ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));
            LOG.warn("m... {}", m);

        }

        ReferenceCountUtil.release(object);
    }
}

