package mmp.im.auth.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendHandler implements IMessageTypeHandler {
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

            LOG.warn("FriendHandler收到消息message... {}", message);
            LOG.warn("FriendHandler收到消息msg... {}", msg);
            // 收到转发的消息
            // List<Info> serverList = SpringContextHolder.getBean(XService.class).getUserServerList(message.getFrom());
            //
            // serverList.forEach((info) -> {
            //     MessageSender.sendToServer(info.getServerInfo(), message);
            //
            // });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

