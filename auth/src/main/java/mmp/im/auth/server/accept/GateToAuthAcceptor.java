package mmp.im.auth.server.accept;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
// public class GateToAuthAcceptor extends AbstractTCPAcceptor {
public class GateToAuthAcceptor {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    //
    // @Override
    public void bind(Integer port) throws InterruptedException {

        ServerBootstrap serverBootstrap;
        EventLoopGroup bossEventLoopGroup;
        EventLoopGroup workerEventLoopGroup;


        bossEventLoopGroup = new NioEventLoopGroup(1);
        workerEventLoopGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap().group(bossEventLoopGroup, workerEventLoopGroup);
        try {
            serverBootstrap
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    // 每隔60s没有接受到read事件的话，则会触发userEventTriggered事件，并指定IdleState的类型为READER_IDLE
                                    // new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),
                                    // // client端设置了每隔30s会发送一个心跳包过来，如果60s都没有收到心跳，则说明链路发生了问题
                                    // new AcceptorIdleStateTrigger(),
                                    // new MessageDecoder(),
                                    // new MessageEncoder(),
                                    // new GateToAuthHandler()

                                    // new EventHandler(null)
                            );
                        }
                    });

            LOG.warn("GateToAuthAcceptor bind...port {} ", port);
            ChannelFuture future = serverBootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOG.warn("GateToAuthAcceptor EventLoopGroup...shutdownGracefully");
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }

    }

    public static void main(String[] args) {
        try {

            new GateToAuthAcceptor().bind(8888);
        } catch (Exception e) {
    e.printStackTrace();
        }
    }
}
