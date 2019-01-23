package mmp.im.auth.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLogoutHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channel, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        try {
            MessageTypeA.Message.ClientLogout msg = message.getData().unpack(MessageTypeA.Message.ClientLogout.class);

            MessageSender.sendToServers(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

