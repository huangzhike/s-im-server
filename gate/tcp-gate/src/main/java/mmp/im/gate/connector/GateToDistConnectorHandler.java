package mmp.im.gate.connector;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;


@Data
@Accessors(chain = true)
@ChannelHandler.Sharable
public class GateToDistConnectorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private MessageHandlerHolder messageHandlerHolder;

    private ConnectorChannelHolder connectorChannelHolder;


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {

        Channel channel = channelHandlerContext.channel();

        if (message instanceof MessageLite) {
            channel.eventLoop().execute(() -> messageHandlerHolder.assignHandler(channelHandlerContext, (MessageLite) message));
        } else {
            // 从InBound里读取的ByteBuf要手动释放，自己创建的ByteBuf要自己负责释放
            // write Bytebuf到OutBound时由netty负责释放，不需要手动调用release
            ReferenceCountUtil.release(message);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelActive... remoteAddress... {} ", channelHandlerContext.channel().remoteAddress());
        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        Channel channel = channelHandlerContext.channel();

        if (channel != null) {
            SocketAddress socketAddress = channel.remoteAddress();
            if (socketAddress != null) {

                LOG.warn("channelInactive... remove remoteAddress... {}", channel.remoteAddress());
                connectorChannelHolder.setChannelHandlerContext(null);

            }
            // 关闭连接
            if (channel.isOpen()) {
                channel.close();
                LOG.warn("channelInactive... close remoteAddress... {}", channel.remoteAddress());
            }
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
            if (state == IdleState.WRITER_IDLE) {
                LOG.warn("IdleState.WRITER_IDLE");
                // 发送心跳
                ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildHeartbeat());
            }
        }

        super.userEventTriggered(channelHandlerContext, evt);
    }

}

