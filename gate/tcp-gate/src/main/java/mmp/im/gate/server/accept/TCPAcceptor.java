package mmp.im.gate.server.accept;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import mmp.im.gate.codec.decode.MessageDecoder;
import mmp.im.gate.codec.encode.MessageEncoder;
import mmp.im.gate.handler.AcceptorIdleStateTrigger;
import mmp.im.gate.handler.EventHandler;
import mmp.im.gate.server.AbstractServer;
import mmp.im.gate.server.IServer;

import mmp.im.gate.handler.InboundHandlerHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class TCPAcceptor extends AbstractServer implements IServer {


    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;


    public TCPAcceptor() {

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


    public void bind(Integer port) throws InterruptedException {

        try {
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    // 每隔60s没有接受到read事件的话，则会触发userEventTriggered事件，并指定IdleState的类型为READER_IDLE
                                    new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),
                                    // client端设置了每隔30s会发送一个心跳包过来，如果60s都没有收到心跳，则说明链路发生了问题
                                    new AcceptorIdleStateTrigger(),
                                    new MessageDecoder(),
                                    new MessageEncoder(),

                                    new InboundHandlerHandler(),
                                    new EventHandler(null)
                            );
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port)).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }

    }


}
