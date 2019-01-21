package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.FRIEND_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Friend msg = message.getData().unpack(ClientMessageBody.ClientMessage.Friend.class);
            System.out.println(msg.getName());
            // 单聊消息
            MessageSender.sendToClient(message.getTo(), message);
            // 其他端有登陆
            MessageSender.sendToServer(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

