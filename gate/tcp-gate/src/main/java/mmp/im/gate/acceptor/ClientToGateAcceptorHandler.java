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
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

@Data
@Accessors(chain = true)
@ChannelHandler.Sharable
// 参考 https://www.jianshu.com/p/93ce43a0eb16
// public class InboundHandlerHandler extends SimpleChannelInboundHandler<Object> {
public class ClientToGateAcceptorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private NettyMessageHandlerHolder NettyMessageHandlerHolder = new NettyMessageHandlerHolder("mmp.im.gate.acceptor.handler", INettyMessageHandler.class);

    private AcceptorChannelManager acceptorChannelMap = AcceptorChannelManager.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {

        Channel channel = channelHandlerContext.channel();

        if (message instanceof MessageLite) {

            channel.eventLoop().execute(() -> NettyMessageHandlerHolder.assignHandler(channelHandlerContext, (MessageLite) message));
        }
        channelHandlerContext.fireChannelRead(message);

    }


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {

        LOG.warn("channelActive... remoteAddress... {}", channelHandlerContext.channel().remoteAddress());

        // 登录后维护一个已接收的消息列表 避免重传造成的重复接收
        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        Channel channel = channelHandlerContext.channel();
        // 客户端用户
        String channelId = channel.attr(AttributeKeyHolder.CHANNEL_ID).get();

        LOG.warn("channelInactive... channelId... {} remoteAddress... {}", channelId, channel.remoteAddress());

        if (channel.isOpen()) {
            LOG.warn("channelInactive... close remoteAddress... {}", channel.remoteAddress());
        }

        if (null != channelId) {
            // 移除连接 关闭连接
            ChannelHandlerContext context = acceptorChannelMap.removeChannel(channelId);
            LOG.warn("channelInactive... remove remoteAddress... {}", channel.remoteAddress());
            // 用户连接的Gate
            // 生成消息待转发
            ClientStatus m = MessageBuilder.buildClientStatus(channelId, Config.SERVER_ID, false, "");
            // distribute
            MessageSender.sendToAcceptor(m);
            // 发的消息待确认
            ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);
        }

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
                LOG.warn("IdleState.READER_IDLE...");
            }
        }
        channelHandlerContext.fireUserEventTriggered(evt);
        super.userEventTriggered(channelHandlerContext, evt);
    }

}
