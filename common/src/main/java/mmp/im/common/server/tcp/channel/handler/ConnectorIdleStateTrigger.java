package mmp.im.common.server.tcp.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                LOG.warn("IdleState.WRITER_IDLE");
                // 发送心跳
                // MessageSender.reply(channelHandlerContext, MessageBuilder.buildHeartbeat());
            }
        } else {
            super.userEventTriggered(channelHandlerContext, evt);
        }
    }
}
