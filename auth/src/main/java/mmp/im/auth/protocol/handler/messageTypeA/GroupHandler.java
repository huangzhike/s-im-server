package mmp.im.auth.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.auth.service.XService;
import mmp.im.auth.util.SpringContextHolder;
import mmp.im.common.model.Info;
import mmp.im.common.model.User;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class GroupHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.GROUP_MESSAGE_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        try {
            MessageTypeA.Message.GroupMessage msg = message.getData().unpack(MessageTypeA.Message.GroupMessage.class);

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

