package mmp.im.auth.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import mmp.im.server.tcp.accept.AbstractTCPAcceptor;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class GateToAuthAcceptor extends AbstractTCPAcceptor {

    @Override
    public void bind(Integer port) throws InterruptedException {

        try {
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    // 每隔60s没有接受到read事件的话，则会触发userEventTriggered事件，并指定IdleState的类型为READER_IDLE
                                    new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS)
                                    // client端设置了每隔30s会发送一个心跳包过来，如果60s都没有收到心跳，则说明链路发生了问题

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
