package mmp.im.common.server.channel.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.server.AbstractConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


@Data
@Accessors(chain = true)
public class ConnectionListener implements ChannelFutureListener {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private AbstractConnector connector;

    public ConnectionListener(AbstractConnector connector) {
        this.connector = connector;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop eventLoop = channelFuture.channel().eventLoop();
            eventLoop.schedule(() -> {
                connector.initBootstrap(eventLoop);
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
