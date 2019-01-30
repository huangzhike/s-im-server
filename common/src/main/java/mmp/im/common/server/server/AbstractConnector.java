package mmp.im.common.server.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractConnector extends AbstractServer {


    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected Bootstrap bootstrap;
    protected int port;
    protected String host;
    protected EventLoopGroup workerEventLoopGroup;

    public AbstractConnector() {
        this.initBootstrap();
    }

    public void initBootstrap(EventLoopGroup workerEventLoopGroup) {
        this.workerEventLoopGroup = workerEventLoopGroup;

        this.bootstrap = new Bootstrap().group(workerEventLoopGroup);
        this.bootstrap.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()))
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(3))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioSocketChannel.class);
    }

    private void initBootstrap() {

        this.initBootstrap(new NioEventLoopGroup());

    }


    protected Object bootstrapLock() {
        return this.bootstrap;
    }

    public abstract void connect();
}
