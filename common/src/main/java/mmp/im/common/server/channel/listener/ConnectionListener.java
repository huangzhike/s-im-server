package mmp.im.common.server.channel.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import mmp.im.common.server.server.AbstractTCPConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ConnectionListener implements ChannelFutureListener {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private AbstractTCPConnector connector;

    public ConnectionListener(AbstractTCPConnector connector) {
        this.connector = connector;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> {
                connector.initBootstrap(loop);
                connector.connect();

                LOG.warn("ChannelFuture operationComplete failed");
                LOG.warn("ChannelFuture start to reconnect....");
            }, 1L, TimeUnit.SECONDS);
        } else {
            LOG.warn("ChannelFuture  operationComplete");
            LOG.warn("ChannelFuture connect successfully....");
        }
    }
}
