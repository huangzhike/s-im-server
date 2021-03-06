package mmp.im.gate.acceptor;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.codec.decode.MessageDecoder;
import mmp.im.common.server.codec.encode.MessageEncoder;
import mmp.im.common.server.server.AbstractAcceptor;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@Data
@Accessors(chain = true)
public class ClientToGateAcceptor extends AbstractAcceptor {

    private int port;

    public ClientToGateAcceptor(Integer port) {
        this.port = port;
    }

    @Override
    public void bind() {

        ChannelHandler[] channelHandler = new ChannelHandler[]{
                new MessageDecoder(),
                new MessageEncoder(),
                new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),
                new ClientToGateAcceptorHandler(),
        };

        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channel.pipeline().addLast(channelHandler);
            }
        });

        try {
            LOG.warn("binding port {}", this.port);
            ChannelFuture future = this.serverBootstrap.bind(new InetSocketAddress(this.port)).sync();
            LOG.warn("closeFuture sync");
            future.channel().closeFuture().sync();
            LOG.warn("future...");
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
        } finally {
            this.bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            this.workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }

    }

}
