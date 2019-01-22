package mmp.im.common.server.tcp.event;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import mmp.im.common.server.tcp.event.Event;
import mmp.im.common.server.tcp.event.EventExecutor;
import mmp.im.common.server.tcp.event.EventType;
import mmp.im.common.server.tcp.event.IEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class EventHandler extends ChannelDuplexHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private EventExecutor eventExecutor;

    public EventHandler(IEventListener listener) {

        this.eventExecutor = new EventExecutor(listener);
        new Thread(this.eventExecutor).start();

    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise future) throws Exception {

        super.connect(ctx, remoteAddress, localAddress, future);
        String r = remoteAddress.toString();
        LOG.warn("EventHandler connect remoteAddress -> {}", r);
        this.eventExecutor.putNettyEvent(new Event(EventType.CONNECT, r, ctx.channel()));
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        super.disconnect(ctx, future);

        LOG.warn("EventHandler disconnect remoteAddress -> {}", remoteAddress);
        this.eventExecutor.putNettyEvent(new Event(EventType.CLOSE, remoteAddress, ctx.channel()));

    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        super.close(ctx, promise);

        LOG.warn("EventHandler close remoteAddress -> {}", remoteAddress);

        this.eventExecutor.putNettyEvent(new Event(EventType.CLOSE, remoteAddress, ctx.channel()));

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        LOG.warn("EventHandler exceptionCaught remoteAddress -> {}", remoteAddress);

        this.eventExecutor.putNettyEvent(new Event(EventType.EXCEPTION, remoteAddress, ctx.channel()));

    }
}
