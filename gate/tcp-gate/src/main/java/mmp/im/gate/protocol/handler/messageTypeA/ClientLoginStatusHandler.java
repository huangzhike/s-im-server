package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLoginStatusHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {

        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGIN_STATUS_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channel, Object object) {


        MessageTypeA.Message message = (MessageTypeA.Message) object;
        try {
            MessageTypeA.Message.ClientLoginStatus msg = message.getData().unpack(MessageTypeA.Message.ClientLoginStatus.class);

            // todo 收到别的端用户登陆信息，保存给用户显示在线状态

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

