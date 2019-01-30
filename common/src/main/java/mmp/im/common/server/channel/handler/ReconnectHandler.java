package mmp.im.common.server.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.server.AbstractConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


@Data
@Accessors(chain = true)
@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private int attempts;

    private AbstractConnector connector;

    public ReconnectHandler(AbstractConnector connector) {
        this.connector = connector;
    }

    /**
     * channel每次active的时候，连接次数重新置零
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelActive attempts = 0...");
        this.attempts = 0;
        channelHandlerContext.fireChannelActive();
    }

    /*
     * 重连
     * */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelInactive...");
        if (this.attempts < 6) {
            ++this.attempts;
            LOG.warn("attempts... {}", this.attempts);
            // 重连
            final EventLoop eventLoop = channelHandlerContext.channel().eventLoop();
            eventLoop.schedule(() -> {
                connector.initBootstrap(eventLoop);
                connector.connect();
            }, 1L, TimeUnit.SECONDS);

            super.channelInactive(channelHandlerContext);

        }
        channelHandlerContext.fireChannelInactive();
    }


}
