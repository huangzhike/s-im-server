package mmp.im.gate.acceptor;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.protocol.handler.NettyMessageHandlerHolder;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static mmp.im.common.protocol.ProtobufMessage.ServerRegister;

@Data
@Accessors(chain = true)
@ChannelHandler.Sharable
public class GateToDistAcceptorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private NettyMessageHandlerHolder NettyMessageHandlerHolder = new NettyMessageHandlerHolder("mmp.im.gate.acceptor.handler", INettyMessageHandler.class);

    private AcceptorChannelManager acceptorChannelMap = AcceptorChannelManager.getInstance();

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        // Channel channel = channelHandlerContext.channel();
        if (message instanceof MessageLite) {

            Channel channel = channelHandlerContext.channel();

            String userId = channel.attr(AttributeKeyConstant.CHANNEL_ID).get();

            String name = message.getClass().toString();

            if (!ServerRegister.getDefaultInstance().getClass().toString().equals(name) && userId == null) {
                LOG.warn("not login yet");
                return;
            }

            executorService.submit(() -> NettyMessageHandlerHolder.assignHandler(channelHandlerContext, (MessageLite) message));
            // channel.eventLoop().execute(() -> NettyMessageHandlerHolder.assignHandler(channelHandlerContext, (MessageLite) message));
        }
        channelHandlerContext.fireChannelRead(message);
    }


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {

        Channel channel = channelHandlerContext.channel();
        // 维护一个已接收的消息列表 避免重传造成重复处理
        channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).set(new ConcurrentHashMap<>());

        LOG.warn("channelActive remoteAddress {}", channelHandlerContext.channel().remoteAddress());

        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        Channel channel = channelHandlerContext.channel();

        // GateId
        String channelId = channel.attr(AttributeKeyConstant.CHANNEL_ID).get();

        LOG.warn("channelInactive... channelId... {} remoteAddress... {}", channelId, channel.remoteAddress());

        if (channel.isOpen()) {
            LOG.warn("channelInactive... close remoteAddress... {}", channel.remoteAddress());
        }

        if (null != channelId) {
            // 移除连接 关闭连接
            ChannelHandlerContext context = acceptorChannelMap.removeChannel(channelId);
            LOG.warn("channelInactive... remove remoteAddress... {}", channel.remoteAddress());
        }
        channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).set(null);
        channelHandlerContext.fireChannelInactive();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        channelHandlerContext.fireExceptionCaught(cause);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                LOG.error("IdleState.READER_IDLE...");
            }
        }
        channelHandlerContext.fireUserEventTriggered(evt);
        super.userEventTriggered(channelHandlerContext, evt);
    }

}

