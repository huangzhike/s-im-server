package mmp.im.common.server.tcp.handler.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

@ChannelHandler.Sharable
public abstract class ReconnectHandler extends ChannelInboundHandlerAdapter implements IChannelHandlerHolder {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final Bootstrap bootstrap;


    private final Timer timer = new Timer();


    private TimerTask timerTask;


    private final int port;

    private final String host;

    private int attempts;

    public ReconnectHandler(Bootstrap bootstrap, String host, int port) {
        this.bootstrap = bootstrap;
        this.port = port;
        this.host = host;
    }

    /**
     * channel链路每次active的时候，连接次数重新置零
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.warn("ReconnectHandler -> channelActive");
        this.attempts = 0;
        ctx.fireChannelActive();
    }

    /*
     * 重连
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.warn("ReconnectHandler -> channelInactive");
        if (this.attempts < 6) {

            LOG.warn("the attempts is {} ", this.attempts);

            // 重连
            this.timer.schedule(  new TimerTask() {

                @Override
                public void run() {
                    ChannelFuture future;
                    // bootstrap初始化后，将handler填入
                    synchronized (bootstrap) {
                        bootstrap.handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline().addLast(handlers());
                            }
                        });
                        LOG.warn("ReconnectHandler executing-> run connect");
                        future = bootstrap.connect(host, port);
                    }
                    // future对象
                    future.addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            // 如果重连失败，则调用ChannelInactive方法
                            LOG.warn("ReconnectHandler -> operationComplete");
                            if (!f.isSuccess()) {
                                LOG.warn("ReconnectHandler -> operationComplete failed");
                                f.channel().pipeline().fireChannelInactive();
                            }
                        }
                    });
                }
            }, ++this.attempts * 100);

        }
        ctx.fireChannelInactive();
    }


}
