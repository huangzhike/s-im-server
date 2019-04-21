package mmp.im.common.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAcceptor extends AbstractServer {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected ServerBootstrap serverBootstrap;
    protected EventLoopGroup bossEventLoopGroup;
    protected EventLoopGroup workerEventLoopGroup;

    public AbstractAcceptor() {
        this.initBootstrap();
    }

    private void initBootstrap() {
        this.bossEventLoopGroup = new NioEventLoopGroup();
        this.workerEventLoopGroup = new NioEventLoopGroup();
        this.serverBootstrap = new ServerBootstrap().group(this.bossEventLoopGroup, this.workerEventLoopGroup);

        this.serverBootstrap
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .childOption(ChannelOption.SO_LINGER, 0)
                .channel(NioServerSocketChannel.class);
        /*
         * 池化的directBuffer
         * 堆外内存，直接内存，减少内存的拷贝，和上下文的切换，但是可能发生堆外内存OOM
         */
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()));

    }

    public abstract void bind();

}
