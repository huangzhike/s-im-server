package mmp.im.gate.acceptor;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
@ChannelHandler.Sharable
// public class InboundHandlerHandler extends SimpleChannelInboundHandler<Object> {
public class ClientToGateAcceptorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProtocolParserHolder protocolParserHolder;

    @Autowired
    private MessageHandlerHolder messageHandlerHolder;

    @Autowired
    private AcceptorChannelHandlerMap acceptorChannelHandlerMap;


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {

        Channel channel = channelHandlerContext.channel();


        byte[] bytes = (byte[]) message;

        channel.eventLoop().execute(() -> {
            IProtocolParser protocolParser = protocolParserHolder.get(bytes[0]);
            if (protocolParser != null) {
                MessageLite msg = (MessageLite) protocolParser.parse(Arrays.copyOfRange(bytes, 1, bytes.length));
                LOG.warn("channelRead parserPacket... {} remoteAddress... {}", protocolParser, channel.remoteAddress());

                if (msg != null) {
                    messageHandlerHolder.assignHandler(channelHandlerContext, msg);
                }
            } else {
                // channel.close();
                LOG.warn("无法识别，通道关闭");
            }
        });

        // 从InBound里读取的ByteBuf要手动释放，自己创建的ByteBuf要自己负责释放
        // write Bytebuf到OutBound时由netty负责释放，不需要手动调用release
        ReferenceCountUtil.release(message);

    }


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        LOG.warn("channelActive... remoteAddress... {}", channelHandlerContext.channel().remoteAddress());
        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        Channel channel = channelHandlerContext.channel();
        // 标识
        String channelId = channel.attr(AttributeKeyHolder.CHANNEL_ID).get();
        LOG.warn("channelInactive... channelId... {} remoteAddress... {}", channelId, channel.remoteAddress());

        if (channel.isOpen()) {
            LOG.warn("channelInactive... close remoteAddress... {}" + channel.remoteAddress());
        }

        if (null != channelId) {
            // 移除连接 关闭连接

            ChannelHandlerContext context = acceptorChannelHandlerMap.removeChannel(channelId);

            LOG.warn("channelInactive... remove remoteAddress... {}" + channel.remoteAddress());
        }


        String serverId = SpringContextHolder.getBean(ClientToGateAcceptor.class).getServeId();

        // ProtobufMessage.Message m = (MessageTypeA.Message) MessageBuilder.buildClientStatus(channelId, "", channelId, serverId, false);

        // distribute
        // SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);

        channelHandlerContext.fireChannelInactive();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        channelHandlerContext.fireExceptionCaught(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        super.userEventTriggered(channelHandlerContext, evt);
    }

}
