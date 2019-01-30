package mmp.im.common.server.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.connection.AcceptorChannelMap;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
@Accessors(chain = true)
public class MessageSender {


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ResendMessageMap resendMessageMap;

    private AcceptorChannelMap acceptorChannelMap;

    private ConnectorChannelHolder connectorChannelHolder;


    public void reply(ChannelHandlerContext channelHandlerContext, MessageLite messageLite) {
        channelHandlerContext.channel().writeAndFlush(messageLite);
    }

    public void sendToConnector(MessageLite messageLite, String key) {

        if (this.acceptorChannelMap != null) {
            ChannelHandlerContext channelHandlerContext = this.acceptorChannelMap.getChannel(key);
            if (channelHandlerContext != null) {
                this.sendTo(messageLite, channelHandlerContext);
            }
        }

    }


    public void sendToAcceptor(MessageLite messageLite) {

        if (this.connectorChannelHolder != null) {
            ChannelHandlerContext channelHandlerContext = this.connectorChannelHolder.getChannelHandlerContext();

            if (channelHandlerContext != null) {
                this.sendTo(messageLite, channelHandlerContext);
            }
        }

    }

    private void sendTo(MessageLite messageLite, ChannelHandlerContext channelHandlerContext) {

        // 发送
        channelHandlerContext.channel().writeAndFlush(messageLite);

        LOG.warn("sendTo... {}", messageLite);

    }


    public void toBeAcknowledged(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, Long seq) {

        this.resendMessageMap.put(seq, new ResendMessage(seq, messageLite, channelHandlerContext));

    }

}
