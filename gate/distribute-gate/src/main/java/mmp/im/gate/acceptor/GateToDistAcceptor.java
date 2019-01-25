package mmp.im.gate.acceptor;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.tcp.channel.handler.AcceptorIdleStateTrigger;
import mmp.im.common.server.tcp.codec.decode.MessageDecoder;
import mmp.im.common.server.tcp.codec.encode.MessageEncoder;
import mmp.im.common.server.tcp.event.EventHandler;
import mmp.im.common.server.tcp.server.AbstractTCPAcceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@Data
@Accessors(chain = true)
public class GateToDistAcceptor extends AbstractTCPAcceptor {

    public GateToDistAcceptor(Integer port) {
        this.port = port;
    }


    @Autowired
    private GateToDistAcceptorHandler gateToDistAcceptorHandler;


    @Override
    public void bind() {
        this.serverBootstrap
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new MessageDecoder(),
                                new MessageEncoder(),
                                // 60s没有read事件，触发userEventTriggered事件，指定IdleState的类型为READER_IDLE
                                // client每隔30s发送一个心跳包，如果60s都没有收到心跳，说明链路发生了问题
                                new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),
                                new AcceptorIdleStateTrigger(),
                                gateToDistAcceptorHandler,
                                new EventHandler(null)
                        );
                    }
                });

        try {
            LOG.warn("bind...port {}", this.port);
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
