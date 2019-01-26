package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.tcp.util.MessageBuilder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.gate.service.StatusService;
import mmp.im.gate.service.XService;
import mmp.im.gate.util.SpringContextHolder;

import java.util.ArrayList;
import java.util.List;


public class GroupMessageHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.GROUP_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;

        MessageTypeA.Message.GroupMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.GroupMessage.class);
            LOG.warn("receive GroupMessage... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        // 查找好友列表 server列表 推送到server

        List<String> serverList = SpringContextHolder.getBean(AcceptorChannelHandlerMap.class).getChannelMapKeyList();


        // 查找好友列表 server列表 推送到server
        List<String> userFriendList = SpringContextHolder.getBean(XService.class).getGroupUserIdList(message.getTo());


        if (serverList != null) {
            for (String serverId : serverList) {
                List<String> serverUserList = new ArrayList<>();
                if (userFriendList != null) {
                    for (String userId : userFriendList) {
                        List<Info> userServerList = SpringContextHolder.getBean(StatusService.class).getUserServerList(userId);
                        if (userServerList != null) {
                            for (Info i : userServerList) {
                                if (serverId.equals(i.getServerInfo())) {
                                    serverUserList.add(userId);
                                }
                            }

                        }
                    }
                }

                MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder
                        .buildBroadcastGroupMessage(message.getFrom(), serverId, msg, serverUserList);
                SpringContextHolder.getBean(MessageSender.class).sendToConnector(m, serverId);


            }

        }


        // 推送到logic处理，数据库操作等

        SpringContextHolder.getBean(MQProducer.class).pub(message);

        // 回复确认
        SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

    }
}

