package mmp.im.auth.server.accept;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import mmp.im.auth.handler.channel.GateToAuthHandler;
import mmp.im.common.server.tcp.codec.decode.MessageDecoder;
import mmp.im.common.server.tcp.codec.encode.MessageEncoder;
import mmp.im.common.server.tcp.event.EventHandler;
import mmp.im.common.server.tcp.handler.channel.AcceptorIdleStateTrigger;
import mmp.im.common.server.tcp.server.accept.AbstractTCPAcceptor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@Component
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
                                    new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),
                                    // client端设置了每隔30s会发送一个心跳包过来，如果60s都没有收到心跳，则说明链路发生了问题
                                    new AcceptorIdleStateTrigger(),
                                    new MessageDecoder(),
                                    new MessageEncoder(),
                                    new GateToAuthHandler(),

                                    new EventHandler(null)
                            );
                        }
                    });

            LOG.warn("GateToAuthAcceptor bind...port {} ", port);
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
