package mmp.im.common.server.tcp.server.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import mmp.im.common.server.tcp.server.AbstractServer;
import mmp.im.common.server.tcp.server.IServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractTCPConnector extends AbstractServer implements IServer {


    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected Bootstrap bootstrap;

    protected EventLoopGroup workerEventLoopGroup;

    public AbstractTCPConnector() {

        ThreadFactory workerFactory = new DefaultThreadFactory("client.connector");

        int workerNum = Runtime.getRuntime().availableProcessors() << 1;

        workerEventLoopGroup = initEventLoopGroup(workerNum, workerFactory);

        // workerEventLoopGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap().group(workerEventLoopGroup);
        bootstrap.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()))
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(3))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .channel(NioSocketChannel.class);

    }
    protected Object bootstrapLock() {
        return bootstrap;
    }
    public abstract void connect(String host, Integer port);
}
