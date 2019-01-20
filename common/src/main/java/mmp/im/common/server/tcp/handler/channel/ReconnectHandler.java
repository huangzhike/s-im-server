package mmp.im.common.server.tcp.handler.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public abstract class ReconnectHandler extends ChannelInboundHandlerAdapter implements TimerTask, IChannelHandlerHolder {

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;

    private final String host;

    private int attempts;

    public ReconnectHandler(Bootstrap bootstrap, Timer timer, int port, String host) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;

    }

    /**
     * channel链路每次active的时候，连接次数重新置零
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        attempts = 0;
        ctx.fireChannelActive();
    }

    /*
     * 重连
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        if (attempts < 6) {
            // 重连间隔越来越长
            timer.newTimeout(this, 2 << attempts++, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        ChannelFuture future;
        // bootstrap初始化后，将handler填入
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        // future对象
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                // 如果重连失败，则调用ChannelInactive方法
                if (!f.isSuccess()) {
                    f.channel().pipeline().fireChannelInactive();
                }
            }
        });

    }

}
