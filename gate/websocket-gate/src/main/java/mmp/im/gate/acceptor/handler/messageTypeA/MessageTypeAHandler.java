package mmp.im.gate.acceptor.handler.messageTypeA;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.server.util.AttributeKeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MessageTypeAHandler {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected void check(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {

        String channelId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();

        if (channelId == null && !(object instanceof MessageTypeA.Message.ClientLogin)) {

            LOG.error("未登录...");
        }

        if (channelId == null && !(object instanceof MessageTypeA.Message.ServerRegister)) {

            LOG.error("未注册...");
        }


    }


}
