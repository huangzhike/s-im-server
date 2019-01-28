package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.gate.service.StatusService;
import mmp.im.gate.util.SpringContextHolder;

import java.util.List;

public class FriendMessageHandler extends MessageTypeAHandler implements IMessageHandler {


    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.FRIEND_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageTypeA.Message.FriendMessage msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.FriendMessage.class);
            LOG.warn("receive FriendMessage... {}", message);
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

        // 单聊消息

        // 查找登陆server列表 推送到server
// todo 自己的别的端也要同步
        List<Info> userServerList = SpringContextHolder.getBean(StatusService.class).getUserServerList(message.getTo());
        // List<String> userServerIdList = new ArrayList<>();
        if (userServerList != null) {
            for (Info info : userServerList) {
                // userServerIdList.add(info.getServerInfo());

                MessageTypeA.Message m = (MessageTypeA.Message) MessageBuilder
                        .buildBroadcastFriendMessage(message.getFrom(), message.getTo(), msg);
                SpringContextHolder.getBean(MessageSender.class).sendToConnector(m, info.getServerInfo());


            }
        }


        // 推送到logic处理，数据库操作等

        SpringContextHolder.getBean(MQProducer.class).pub(message);

        // 回复确认
        SpringContextHolder.getBean(MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

    }
}

