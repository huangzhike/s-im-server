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

    private Long serveId;
    private ClientToGateAcceptorHandler acceptorHandler;

    public ClientToGateAcceptor(Integer port) {
        this.port = port;
    }
    @Override
    public void bind() {
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(
                        new ChannelHandler[]{
                                new MessageDecoder(),
                                new MessageEncoder(),
                                // 60s没有read事件，触发userEventTriggered事件，指定IdleState的类型为READER_IDLE
                                // client每隔30s发送一个心跳包，如果60s都没有收到心跳，说明链路发生了问题
                                acceptorHandler,
                                new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),

                        }
                );
            }
        });

        try {
            LOG.warn("binding port... {}", this.port);
            ChannelFuture future = this.serverBootstrap.bind(new InetSocketAddress(this.port)).sync();
            LOG.warn("closeFuture sync...");
            future.channel().closeFuture().sync();
            LOG.warn("future...");
        } catch (Exception e) {
            LOG.error("bind Exception... {}", e);
        } finally {
            this.bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            this.workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }

    }

}
