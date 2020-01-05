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
import mmp.im.common.util.seq.SeqGenerator;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

public class GroupMessageHandler  implements INettyMessageHandler {

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

        LOG.warn("GroupMessage... {}", message);
        // server列表 推送到server
        List<String> serverList = AcceptorChannelManager.getInstance().getChannelMapKeyList();
        serverList = Optional.ofNullable(serverList).orElse(new ArrayList<>());
        LOG.warn("serverList... {}", serverList);

        // 查找群好友列表
        // 自己的别的端也要同步
        List<String> userFriendList = ContextHolder.getXService().getGroupUserIdList(message.getTo());
        userFriendList = Optional.ofNullable(userFriendList).orElse(new ArrayList<>());
        LOG.warn("userFriendList... {}", userFriendList);

        for (String gateId : serverList) {
            List<String> serverUserList = new ArrayList<>();

            for (String userId : userFriendList) {
                Set<String> userServerList = ContextHolder.getStatusService().getUserServerList(userId);
                userServerList = Optional.ofNullable(userServerList).orElse(new HashSet<>());

                for (String i : userServerList) {
                    if (gateId.equals(i)) {
                        // Gate连接某个用户
                        serverUserList.add(userId);
                    }
                }
            }

            // 生成序列号
            GroupMessage m = MessageBuilder.buildTransGroupMessage(message, serverUserList, SeqGenerator.get());

            // 发送
            MessageSender.sendToConnector(m, gateId);

            // 发的消息待确认
            ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);

            LOG.warn("m... {}", m);
        }



    }
}

