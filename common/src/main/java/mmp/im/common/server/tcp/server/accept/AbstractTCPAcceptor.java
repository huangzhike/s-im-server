package mmp.im.common.server.tcp.server.accept;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import mmp.im.common.server.tcp.server.AbstractServer;
import mmp.im.common.server.tcp.server.IServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTCPAcceptor extends AbstractServer implements IServer {


    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected ServerBootstrap serverBootstrap;
    protected EventLoopGroup bossEventLoopGroup;
    protected EventLoopGroup workerEventLoopGroup;


    public AbstractTCPAcceptor() {

        bossEventLoopGroup = initEventLoopGroup(1, new DefaultThreadFactory("netty.acceptor.boss"));
        int workerNum = Runtime.getRuntime().availableProcessors() << 1;
        workerEventLoopGroup = initEventLoopGroup(workerNum, new DefaultThreadFactory("netty.acceptor.worker"));

        // bossEventLoopGroup = new NioEventLoopGroup();
        // workerEventLoopGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap().group(bossEventLoopGroup, workerEventLoopGroup);

        serverBootstrap
                .option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true) // 调试用
                .childOption(ChannelOption.SO_KEEPALIVE, true); // 心跳机制暂时使用TCP选项，之后再自己实现


        /*
         * 池化的directBuffer
         * 一般高性能的场景下使用的堆外内存，也就是直接内存，好处就是减少内存的拷贝，和上下文的切换
         * 缺点是容易发生堆外内存OOM
         */
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()));

    }

    public abstract void bind(Integer port);

}
