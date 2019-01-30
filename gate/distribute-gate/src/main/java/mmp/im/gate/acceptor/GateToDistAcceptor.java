package mmp.im.gate.acceptor;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.server.AbstractAcceptor;

import java.net.InetSocketAddress;


@Data
@Accessors(chain = true)
public class GateToDistAcceptor extends AbstractAcceptor {

    public GateToDistAcceptor(Integer port) {
        this.port = port;
    }

    private String serveId;

    private ChannelInitializer channelInitializer;


    @Override
    public void bind() {
        this.serverBootstrap.childHandler(this.channelInitializer);

        LOG.warn("childHandler... {}", this.channelInitializer);

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
