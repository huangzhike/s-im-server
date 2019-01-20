package mmp.im.common.server.tcp.accept;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import mmp.im.common.server.tcp.AbstractServer;
import mmp.im.common.server.tcp.IServer;

public abstract class AbstractTCPAcceptor extends AbstractServer implements IServer {


    protected ServerBootstrap serverBootstrap;
    protected EventLoopGroup bossEventLoopGroup;
    protected EventLoopGroup workerEventLoopGroup;


    public AbstractTCPAcceptor() {

        bossEventLoopGroup = initEventLoopGroup(1, new DefaultThreadFactory("netty.acceptor.boss"));
        workerEventLoopGroup = initEventLoopGroup(Runtime.getRuntime().availableProcessors() << 1,
                new DefaultThreadFactory("netty.acceptor.worker"));
        serverBootstrap = new ServerBootstrap().group(bossEventLoopGroup, workerEventLoopGroup);

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true) // 调试用
                .childOption(ChannelOption.SO_KEEPALIVE, true); // 心跳机制暂时使用TCP选项，之后再自己实现


        // 使用池化的directBuffer
        /*
         * 一般高性能的场景下,使用的堆外内存，也就是直接内存，使用堆外内存的好处就是减少内存的拷贝，和上下文的切换，缺点是
         * 堆外内存处理的不好容易发生堆外内存OOM
         */

        // ByteBufAllocator 配置
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(PlatformDependent.directBufferPreferred()));

    }


    public abstract void bind(Integer port) throws InterruptedException;


}
