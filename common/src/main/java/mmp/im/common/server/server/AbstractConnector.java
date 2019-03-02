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

    protected EventLoopGroup workerEventLoopGroup;

    public AbstractConnector() {
        this.initBootstrap();
    }

    private void initBootstrap() {

        this.workerEventLoopGroup = new NioEventLoopGroup();

        this.bootstrap = new Bootstrap().group(this.workerEventLoopGroup);
        this.bootstrap.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()))
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(3))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioSocketChannel.class);
    }

    public abstract void connect();
}
