package mmp.im.common.server.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import mmp.im.common.server.server.AbstractTCPConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private int attempts;

    private AbstractTCPConnector connector;

    public ReconnectHandler(AbstractTCPConnector connector) {
        this.connector = connector;
    }

    /**
     * channel链路每次active的时候，连接次数重新置零
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.warn("channelActive attempts = 0...");
        this.attempts = 0;
        ctx.fireChannelActive();
    }

    /*
     * 重连
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.warn("channelInactive...");
        if (this.attempts < 6) {
            ++this.attempts;

            LOG.warn("attempts... {}", this.attempts);
            // 重连

            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> {
                connector.initBootstrap(eventLoop);
                connector.connect();
            }, 1L, TimeUnit.SECONDS);

            super.channelInactive(ctx);

        }
        ctx.fireChannelInactive();
    }


}
