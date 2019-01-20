package mmp.im.common.server.tcp.handler.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import mmp.im.common.server.tcp.event.Event;
import mmp.im.common.server.tcp.event.EventExecutor;
import mmp.im.common.server.tcp.event.EventType;
import mmp.im.common.server.tcp.event.IEventListener;

import java.net.SocketAddress;

public class EventHandler extends ChannelDuplexHandler {

    private EventExecutor eventExecutor;

    public EventHandler(IEventListener listener) {

        this.eventExecutor = new EventExecutor(listener);
        new Thread(this.eventExecutor).start();

    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) throws Exception {

        super.connect(ctx, remoteAddress, localAddress, future);
        String r = remoteAddress.toString();
        this.eventExecutor.putNettyEvent(new Event(EventType.CONNECT, r, ctx.channel()));
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        super.disconnect(ctx, future);
        this.eventExecutor.putNettyEvent(new Event(EventType.CLOSE, remoteAddress, ctx.channel()));

    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();

        super.close(ctx, promise);
        this.eventExecutor.putNettyEvent(new Event(EventType.CLOSE, remoteAddress, ctx.channel()));

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        this.eventExecutor.putNettyEvent(new Event(EventType.EXCEPTION, remoteAddress, ctx.channel()));

    }
}
