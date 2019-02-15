package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import mmp.im.gate.util.SeqGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

public class GroupMessageHandler  extends CheckHandler  implements INettyMessageHandler {

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

        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));


        // 说明是重复发送，不处理，只回复ACK
        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");
            // release
            return;
        }
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());

        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().pub(message);

        LOG.warn("GroupMessage... {}", message);
        // server列表 推送到server
        List<String> serverList = ContextHolder.getAcceptorChannelMap().getChannelMapKeyList();


        LOG.warn("serverList... {}", serverList);
        // 查找群好友列表
        List<String> userFriendList = ContextHolder.getXService().getGroupUserIdList(message.getTo());


        LOG.warn("userFriendList... {}", userFriendList);
        // 自己的别的端也要同步
        // List<Info> selfServerList = ContextHolder.getStatusService().getUserServerList(message.getFrom());
        // if (selfServerList != null) {
        //     selfServerList.forEach(i -> userFriendList.add(i.getServerInfo()));
        // }
        // LOG.warn("selfServerList... {}", selfServerList);

        if (serverList != null) {
            for (String serverId : serverList) {
                // 某个Gate
                List<String> serverUserList = new ArrayList<>();
                if (userFriendList != null) {
                    for (String userId : userFriendList) {
                        List<Info> userServerList = ContextHolder.getStatusService().getUserServerList(userId);
                        if (userServerList != null) {
                            for (Info i : userServerList) {
                                if (serverId.equals(i.getServerInfo())) {
                                    // Gate连接某个用户
                                    serverUserList.add(userId);
                                }
                            }

                        }
                    }
                }
                // 生成序列号
                GroupMessage m = MessageBuilder.buildTransGroupMessage(message, serverUserList, SeqGenerator.get());
                ContextHolder.getMessageSender().sendToConnector(m, serverId);
                // 发的消息待确认
                ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));
                LOG.warn("m... {}", m);
            }

        }

        ReferenceCountUtil.release(object);
    }
}

