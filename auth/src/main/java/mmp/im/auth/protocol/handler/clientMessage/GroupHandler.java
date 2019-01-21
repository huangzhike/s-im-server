package mmp.im.auth.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.auth.service.XService;
import mmp.im.auth.util.SpringContextHolder;
import mmp.im.common.model.Info;
import mmp.im.common.model.User;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class GroupHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(ClientMessageBody.ClientMessage.MessageType.GROUP_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        ClientMessageBody.ClientMessage message = (ClientMessageBody.ClientMessage) object;
        try {
            ClientMessageBody.ClientMessage.Group msg = message.getData().unpack(ClientMessageBody.ClientMessage.Group.class);
            System.out.println(msg.getName());


            List<User> userList = null;
            if (userList != null) {
                userList.forEach(user -> {
                    // 收到转发的消息
                    List<Info> serverList = SpringContextHolder.getBean(XService.class).getUserServerList(user.getId());

                    serverList.forEach((info) -> {
                        MessageSender.sendToServer(info.getServerInfo(), message);

                    });
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

