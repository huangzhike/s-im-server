package mmp.im.gate.connector;

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
import mmp.im.common.server.connection.ConnectorChannelHolder;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static mmp.im.common.protocol.ProtobufMessage.ServerRegister;

@Data
@Accessors(chain = true)
@ChannelHandler.Sharable
public class GateToDistConnectorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private NettyMessageHandlerHolder NettyMessageHandlerHolder = new NettyMessageHandlerHolder("mmp.im.gate.connector.handler", INettyMessageHandler.class);

    private ConnectorChannelHolder connectorChannelHolder = ConnectorChannelHolder.getInstance();

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {

        // 处理消息
        if (message instanceof MessageLite) {
            executorService.submit(() -> NettyMessageHandlerHolder.assignHandler(channelHandlerContext, (MessageLite) message));
        }
        channelHandlerContext.fireChannelRead(message);

    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelActive... remoteAddress... {} ", channelHandlerContext.channel().remoteAddress());

        Channel channel = channelHandlerContext.channel();

        // 维护一个已接收的消息列表 避免重传造成重复处理
        channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).set(new ConcurrentHashMap<>());

        // 向服务器注册
        ServerRegister message = MessageBuilder.buildServerRegister(Config.SERVER_ID, Config.TOKEN);
        MessageSender.reply(channelHandlerContext, message);

        // 发的消息待确认
        ResendMessageManager.getInstance().put(message.getSeq(), message, channelHandlerContext);

        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {

        Channel channel = channelHandlerContext.channel();

        if (channel != null) {
            // 清除消息列表
            channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).set(null);
            SocketAddress socketAddress = channel.remoteAddress();
            if (socketAddress != null) {
                LOG.warn("channelInactive remove remoteAddress {}", channel.remoteAddress());
                // 清除
                connectorChannelHolder.setChannelHandlerContext(null);
            }

            // 关闭连接
            if (channel.isOpen()) {
                channel.close();
                LOG.warn("channelInactive close remoteAddress {}", channel.remoteAddress());
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
                LOG.warn("userEventTriggered IdleState.WRITER_IDLE...");
                // 发送心跳
                MessageSender.reply(channelHandlerContext, MessageBuilder.buildHeartbeat());
            }
        }
        channelHandlerContext.fireUserEventTriggered(evt);
        super.userEventTriggered(channelHandlerContext, evt);
    }

}

