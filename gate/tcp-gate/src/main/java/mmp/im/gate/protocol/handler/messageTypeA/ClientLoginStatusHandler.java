package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;

public class ClientLoginStatusHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGIN_STATUS_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channel, Object object) {

        MessageTypeA.Message message = (MessageTypeA.Message) object;
        MessageTypeA.Message.ClientLoginStatus msg = null;
        try {
            msg = message.getData().unpack(MessageTypeA.Message.ClientLoginStatus.class);
            // todo 收到别的端用户登陆信息，保存给用户显示在线状态
        } catch (Exception e) {
            LOG.error("unpack Exception... {}", e);
        }

    }
}

