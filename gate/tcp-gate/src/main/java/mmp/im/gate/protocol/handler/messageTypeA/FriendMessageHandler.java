package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendMessageHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.FRIEND_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        try {
            MessageTypeA.Message.FriendMessage msg = message.getData().unpack(MessageTypeA.Message.FriendMessage.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 单聊消息
        MessageSender.sendToClient(message.getTo(), message);
        // 其他端有登陆
        MessageSender.sendToServers(message);

    }
}

