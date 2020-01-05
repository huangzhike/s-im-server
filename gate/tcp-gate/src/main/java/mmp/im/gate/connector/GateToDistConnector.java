package mmp.im.gate.connector;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.channel.handler.ReconnectHandler;
import mmp.im.common.server.channel.listener.ConnectionListener;
import mmp.im.common.server.codec.decode.MessageDecoder;
import mmp.im.common.server.codec.encode.MessageEncoder;
import mmp.im.common.server.server.AbstractConnector;

import java.util.concurrent.TimeUnit;


@Data
@Accessors(chain = true)
public class GateToDistConnector extends AbstractConnector {


    private int port;

    private String host;

    public GateToDistConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() {

        ChannelFuture future;

        ChannelHandler[] channelHandlers = new ChannelHandler[]{
                new MessageDecoder(),
                new MessageEncoder(),
                new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                new GateToDistConnectorHandler(),
                new ReconnectHandler(this)
        };

        try {
            synchronized (this) {
                this.bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        channel.pipeline().addLast(channelHandlers);
                    }
                });
                LOG.warn("connect... host {} ...  port {} ... ", host, port);
                // 连接（服务端bind，客户端connect）
                // future = this.bootstrap.connect(host, port).sync();
                future = this.bootstrap.connect(host, port);

                future.addListener(new ConnectionListener(this));
            }
            // 连接关闭
            LOG.warn("closeFuture... sync... ");
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
        } finally {
            // this.workerEventLoopGroup.shutdownGracefully();
            // LOG.warn("shutdownGracefully... ");
        }

    }

}


/*
 * 之前没注意，pipeline是有顺序的
 * 参考这篇文章 https://www.jianshu.com/p/a8a0acfdc96c
 * 还有这篇 https://www.jianshu.com/p/96a50869b527
 * */

/*
 *  添加的自定义ChannelHandler会插入到head和tail之间
 *  如果是ChannelInboundHandler的回调，根据插入的顺序进行链式调用
 *  ChannelOutboundHandler则相反
 * */


/*
 * 假设在 pipeline 里有三个 handlers , 它们都都拦截 close() 方法操作, 并且在面里调用 ctx.close()
 *
 * ChannelPipeline p = ...;
 * p.addLast("A", new SomeHandler());
 * p.addLast("B", new SomeHandler());
 * p.addLast("C", new SomeHandler());
 *
 * public class SomeHandler extends ChannelOutboundHandlerAdapter {
 *     @Override
 *     public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
 *         ctx.close(promise);
 *     }
 * }
 * Channel.close() 会触发 C.close() , B.close(), A.close(), 然后再关闭 channel
 * ChannelPipeline.context("C").close() 会触发 B.close(), A.close(), 然后再关闭 channel
 * ChannelPipeline.context("B").close() 会触发 A.close(), 然后再关闭 channel
 * ChannelPipeline.context("A").close() 则会直接关闭 channel. 不再会有 handlers 调用了
 * */


/*
 * @Override
 * protected void initChannel(final Channel ch) {
 *         ChannelPipeline p = ch.pipeline();
 *         p.addLast(new HttpRequestDecoder());
 *         p.addLast(new HttpResponseEncoder());
 *         p.addLast(new InHandler1());
 *         p.addLast(new InHandler2());
 *         p.addLast(new OutHandler1());
 *         p.addLast(new OutHandler2());
 * }
 * 如果在 InHandler2 中, 调用了 write(...) 则不会触发调用 OutHandler2 和 OutHandler1 的
 * 因为 ctx.write(...) 只会触发离它最近的 out handler, 但是, InHandler2 前面没有 out handler了
 * 但如果通过 channel.write(...)的话, 则它会从 OutHandler2 -> OutHandler1
 * */
