package mmp.im.auth.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.auth.service.XService;
import mmp.im.auth.util.SpringContextHolder;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.MessageSender;

import java.util.List;

public class FriendHandler implements IMessageTypeHandler {


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
            // 收到转发的消息
            List<Info> serverList = SpringContextHolder.getBean(XService.class).getUserServerList(message.getFrom());

            serverList.forEach((info) -> {
                MessageSender.sendToServer(info.getServerInfo(), message);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

