package mmp.im.gate.protocol.handler.clientMessage;

import io.netty.channel.ChannelHandlerContext;
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

            MessageSender.sendToServer(message);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

