package mmp.im.common.server.tcp.event;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
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
    public void connect(ChannelHandlerContext channelHandlerContext, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise future) throws Exception {

        super.connect(channelHandlerContext, remoteAddress, localAddress, future);
        LOG.warn("connect remoteAddress... {}", remoteAddress);
        this.eventExecutor.putNettyEvent(new Event(EventType.CONNECT, remoteAddress.toString(), channelHandlerContext.channel()));
    }

    @Override
    public void disconnect(ChannelHandlerContext channelHandlerContext, ChannelPromise future) throws Exception {
        String remoteAddress = channelHandlerContext.channel().remoteAddress().toString();
        super.disconnect(channelHandlerContext, future);
        LOG.warn("disconnect remoteAddress... {}", remoteAddress);
        this.eventExecutor.putNettyEvent(new Event(EventType.CLOSE, remoteAddress, channelHandlerContext.channel()));

    }

    @Override
    public void close(ChannelHandlerContext channelHandlerContext, ChannelPromise promise) throws Exception {
        String remoteAddress = channelHandlerContext.channel().remoteAddress().toString();
        super.close(channelHandlerContext, promise);
        LOG.warn("close remoteAddress... {}", remoteAddress);
        this.eventExecutor.putNettyEvent(new Event(EventType.CLOSE, remoteAddress, channelHandlerContext.channel()));

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        String remoteAddress = channelHandlerContext.channel().remoteAddress().toString();
        LOG.warn("exceptionCaught remoteAddress... {}", remoteAddress);
        this.eventExecutor.putNettyEvent(new Event(EventType.EXCEPTION, remoteAddress, channelHandlerContext.channel()));
    }
}
