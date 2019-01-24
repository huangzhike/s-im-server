package mmp.im.common.server.tcp.server.accept;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import mmp.im.common.server.tcp.server.AbstractServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTCPAcceptor extends AbstractServer {


    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected ServerBootstrap serverBootstrap;
    protected EventLoopGroup bossEventLoopGroup;
    protected EventLoopGroup workerEventLoopGroup;
    protected int port;

    public AbstractTCPAcceptor() {
        this.initBootstrap();
    }

    private void initBootstrap(EventLoopGroup bossEventLoopGroup, EventLoopGroup workerEventLoopGroup) {
        this.bossEventLoopGroup = bossEventLoopGroup;
        this.workerEventLoopGroup = workerEventLoopGroup;
        // bossEventLoopGroup = new NioEventLoopGroup();
        // workerEventLoopGroup = new NioEventLoopGroup();
        this.serverBootstrap = new ServerBootstrap().group(bossEventLoopGroup, workerEventLoopGroup);

        this.serverBootstrap
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true) // 调试用
                .childOption(ChannelOption.SO_KEEPALIVE, true); // 心跳机制使用TCP选项


        /*
         * 池化的directBuffer
         * 一般高性能的场景下使用的堆外内存，也就是直接内存，好处就是减少内存的拷贝，和上下文的切换
         * 缺点是容易发生堆外内存OOM
         */
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()));
    }


    private void initBootstrap() {
        EventLoopGroup bossEventLoopGroup = this.initEventLoopGroup(1, new DefaultThreadFactory("netty.acceptor.boss"));
        int workerNum = Runtime.getRuntime().availableProcessors() << 1;
        EventLoopGroup workerEventLoopGroup = this.initEventLoopGroup(workerNum, new DefaultThreadFactory("netty.acceptor.worker"));

        this.initBootstrap(bossEventLoopGroup, workerEventLoopGroup);
    }


    public abstract void bind();

}
