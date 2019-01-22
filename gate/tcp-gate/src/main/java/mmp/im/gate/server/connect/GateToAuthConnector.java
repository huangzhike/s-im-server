package mmp.im.gate.server.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import mmp.im.common.server.tcp.codec.decode.MessageDecoder;
import mmp.im.common.server.tcp.codec.encode.MessageEncoder;
import mmp.im.common.server.tcp.handler.channel.ConnectorIdleStateTrigger;
import mmp.im.common.server.tcp.handler.channel.ReconnectHandler;
import mmp.im.common.server.tcp.server.connect.AbstractTCPConnector;
import mmp.im.gate.handler.channel.GateToAuthHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class GateToAuthConnector extends AbstractTCPConnector {


    @Override
    public void connect(String host, Integer port) {

        Bootstrap boot = bootstrap;


        // 重连，前提是已经连接上了
        ReconnectHandler reconnectHandler = new ReconnectHandler(boot, host, port) {

            public ChannelHandler[] handlers() {
                // handler 对象数组
                return new ChannelHandler[]{
                        this,
                        // 之前没注意，这个pipeline是有顺序的
                        new MessageDecoder(),
                        new MessageEncoder(),
                        // 每隔30s触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                        new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                        // 实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端
                        new ConnectorIdleStateTrigger(),
                        new GateToAuthHandler()


                };
            }
        };


        try {
            ChannelFuture future;
            synchronized (bootstrapLock()) {
                boot.handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(reconnectHandler.handlers());
                    }
                });
                LOG.warn("GateToAuthConnector connect... host -> {} port -> {}", host, port);
                // 发起异步连接操作（服务端bind，客户端connect）
                future = boot.connect(host, port).sync();
                LOG.warn("GateToAuthConnector connect... sync... " );
            }
            // 当链路关闭
            future.channel().closeFuture().sync();
            LOG.warn("GateToAuthConnector closeFuture... sync... " );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerEventLoopGroup.shutdownGracefully();
        }

    }

}
