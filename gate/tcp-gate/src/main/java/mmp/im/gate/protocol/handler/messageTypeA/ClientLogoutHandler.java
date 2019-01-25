package mmp.im.gate.protocol.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.server.tcp.util.MessageSender;
import mmp.im.gate.util.SpringContextHolder;

public class ClientLogoutHandler extends MessageTypeAHandler implements IMessageTypeHandler {


    @Override
    public String getHandlerName() {
        return String.valueOf(MessageTypeA.Message.Type.CLIENT_LOGOUT_VALUE);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageTypeA.Message message = (MessageTypeA.Message) object;

        // 从AUTH获取用户TOKEN对比

        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();

        // 直接关闭连接，不发送确认了
        SpringContextHolder.getBean("", AcceptorChannelHandlerMap.class).removeChannel(userId);
        // channelHandlerContext.channel().close();
        // 发给Auth


        SpringContextHolder.getBean("", MessageSender.class).sendTo(message, null);


    }
}

