package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import mmp.im.gate.util.SeqGenerator;

import java.util.ArrayList;
import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

public class GroupMessageHandler implements INettyMessageHandler {


    private final String name = GroupMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {


        GroupMessage message = (GroupMessage) object;

        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().pub(message);


        // 查找好友列表 server列表 推送到server

        List<String> serverList = ContextHolder.getAcceptorChannelMap().getChannelMapKeyList();

        // TODO 自己的别的端也要同步
        // 查找好友列表 server列表 推送到server
        List<String> userFriendList = ContextHolder.getXService().getGroupUserIdList(message.getTo());


        if (serverList != null) {
            for (String serverId : serverList) {
                List<String> serverUserList = new ArrayList<>();
                if (userFriendList != null) {
                    for (String userId : userFriendList) {
                        List<Info> userServerList = ContextHolder.getStatusService().getUserServerList(userId);
                        if (userServerList != null) {
                            for (Info i : userServerList) {
                                if (serverId.equals(i.getServerInfo())) {
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


            }

        }

        ReferenceCountUtil.release(object);
    }
}

