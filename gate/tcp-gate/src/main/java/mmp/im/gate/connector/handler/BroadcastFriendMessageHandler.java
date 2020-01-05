package mmp.im.gate.connector.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;

public class BroadcastFriendMessageHandler  implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = FriendMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();

        FriendMessage message = (FriendMessage) object;

        LOG.warn("FriendMessage... {}", message);

        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        Map<Long, Long> receivedCache = channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).get();

        if (receivedCache.containsKey(message.getSeq())) {
            LOG.warn("repeat");
            return;
        }

        // 加入已收到的消息
        receivedCache.putIfAbsent(message.getSeq(), message.getSeq());

        FriendMessage m = MessageBuilder.buildTransFriendMessage(message);
        // 下发
        MessageSender.sendToConnector(m, message.getTo());
        // 发的消息待确认
        ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);

        LOG.warn("m... {}", m);


    }
}

