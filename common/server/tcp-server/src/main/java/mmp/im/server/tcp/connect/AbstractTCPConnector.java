package mmp.im.server.tcp.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import mmp.im.server.tcp.AbstractServer;
import mmp.im.server.tcp.IServer;

import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractTCPConnector extends AbstractServer implements IServer {


    protected Bootstrap bootstrap;
    protected EventLoopGroup workerEventLoopGroup;

    protected Object bootstrapLock() {
        return bootstrap;
    }

    public AbstractTCPConnector() {

        ThreadFactory workerFactory = new DefaultThreadFactory("client.connector");

        int workerNum = Runtime.getRuntime().availableProcessors() << 1;

        workerEventLoopGroup = initEventLoopGroup(workerNum, workerFactory);
        ByteBufAllocator allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        bootstrap = new Bootstrap().group(workerEventLoopGroup);
        bootstrap.option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(3))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .channel(NioSocketChannel.class);

    }


    public abstract void connect(Integer port, String host);
}
