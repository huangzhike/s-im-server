package mmp.im.gate.server.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import mmp.im.gate.codec.decode.MessageDecoder;
import mmp.im.gate.codec.encode.MessageEncoder;
import mmp.im.gate.handler.ConnectorIdleStateTrigger;
import mmp.im.gate.handler.ReconnectHandler;
import mmp.im.server.tcp.connect.AbstractTCPConnector;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GateToAuthConnector extends AbstractTCPConnector {


    @Override
    public void connect(Integer port, String host) {

        Bootstrap boot = bootstrap;

        final HashedWheelTimer reconnectTimer = new HashedWheelTimer(new ThreadFactory() {

            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ConnectorExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
        // 重连，前提是已经连接上了
        ReconnectHandler reconnectHandler = new ReconnectHandler(boot, reconnectTimer, port, host) {

            public ChannelHandler[] handlers() {
                // handler 对象数组
                return new ChannelHandler[]{
                        this,
                        // 每隔30s触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                        new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                        // 实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端
                        new ConnectorIdleStateTrigger(),
                        new MessageDecoder(),
                        new MessageEncoder()
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

                // 发起异步连接操作（服务端bind，客户端connect）
                future = boot.connect(host, port);
            }
            future.sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerEventLoopGroup.shutdownGracefully();
        }

    }

}
