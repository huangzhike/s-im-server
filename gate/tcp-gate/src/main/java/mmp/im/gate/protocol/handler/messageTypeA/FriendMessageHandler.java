package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageBuilder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.gate.util.SpringContextHolder;

public class FriendMessageHandler extends MessageTypeAHandler implements IMessageTypeHandler {


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
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }


        SpringContextHolder.getBean("", MessageSender.class).sendTo(message, null);
        // 单聊消息

        // 其他端有登陆


        // 推送到logic处理，数据库操作等

        SpringContextHolder.getBean(MQProducer.class).publish("", "", message);

        // 回复确认
        SpringContextHolder.getBean("", MessageSender.class).reply(channelHandlerContext, MessageBuilder.buildAcknowlege(message.getSeq()));

    }
}

