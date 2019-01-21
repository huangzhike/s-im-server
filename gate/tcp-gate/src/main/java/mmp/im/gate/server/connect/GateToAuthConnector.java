package mmp.im.gate.server.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import mmp.im.common.server.tcp.codec.decode.MessageDecoder;
import mmp.im.common.server.tcp.codec.encode.MessageEncoder;
import mmp.im.common.server.tcp.handler.channel.ReconnectHandler;
import mmp.im.common.server.tcp.server.connect.AbstractTCPConnector;
import mmp.im.gate.handler.channel.GateToAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
// public class GateToAuthConnector extends AbstractTCPConnector {
public class GateToAuthConnector   {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    Bootstrap bootstrap;
    protected Object bootstrapLock() {
        return bootstrap;
    }
    // @Override
    public void connect(String host, Integer port) {


          EventLoopGroup workerEventLoopGroup;

        workerEventLoopGroup =   new NioEventLoopGroup();

        bootstrap = new Bootstrap().group(workerEventLoopGroup);

        // 重连，前提是已经连接上了
        ReconnectHandler reconnectHandler = new ReconnectHandler(bootstrap, host, port) {

            public ChannelHandler[] handlers() {
                // handler 对象数组
                return new ChannelHandler[]{
                        this,
                        // 每隔30s触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                        // new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                        // 实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端
                        // new ConnectorIdleStateTrigger(),
                        new GateToAuthHandler(),
                        // new MessageDecoder(),
                        // new MessageEncoder()
                };
            }
        };


        try {
            ChannelFuture future;
            // synchronized (bootstrapLock()) {
                bootstrap
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(reconnectHandler.handlers());
                                // ch.pipeline().addLast( new GateToAuthHandler());
                            }
                        });
                LOG.warn("GateToAuthConnector connect... host -> {} port -> {} ", host, port);
                // 发起异步连接操作（服务端bind，客户端connect）
                future = bootstrap.connect(host, port).sync();
            // }
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerEventLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {

      new  GateToAuthConnector().connect("127.0.0.1",8888);
    }

}
