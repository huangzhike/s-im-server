package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.seq.SeqGenerator;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;

public class FriendMessageHandler   implements INettyMessageHandler {

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
        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        // 说明是重复发送，不处理，只回复ACK
        Map<Long, Long> receivedCache = channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).get();

        if (receivedCache.containsKey(message.getSeq())) {
            LOG.warn("repeat");
            return;
        }

        // 加入已收到的消息
        receivedCache.putIfAbsent(message.getSeq(), message.getSeq());

        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().publish(message);

        // 单聊消息
        LOG.warn("FriendMessage... {}", message);

        // 查找好友登陆server列表 推送到server
        Set<String> friendServerList = ContextHolder.getStatusService().getUserServerList(message.getTo());

        LOG.warn("friendServerList... {}", friendServerList);

        // 自己的别的端也要同步
        Set<String> selfServerList = ContextHolder.getStatusService().getUserServerList(message.getFrom());

        LOG.warn("selfServerList... {}", selfServerList);

        friendServerList = Optional.ofNullable(friendServerList).orElse(new HashSet<>());

        selfServerList = Optional.ofNullable(selfServerList).orElse(new HashSet<>());

        friendServerList.addAll(selfServerList);

        for (String serverId : friendServerList) {
            // 生成序列号
            FriendMessage m = MessageBuilder.buildTransFriendMessage(message, SeqGenerator.get());
            // 发送
            MessageSender.sendToConnector(m, serverId);
            // 发的消息待确认
            ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);
            LOG.warn("m... {}", m);
        }


    }
}

