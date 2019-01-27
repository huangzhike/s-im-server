package mmp.im.common.server.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
@Accessors(chain = true)
public class MessageSender {


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ResendMessageMap resendMessageMap;

    private AcceptorChannelHandlerMap acceptorChannelHandlerMap;

    private ConnectorChannelHolder connectorChannelHolder;

    public void reply(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageLite messageLite = (MessageLite) object;
        channelHandlerContext.channel().writeAndFlush(messageLite);
        toBeAcknowledgedIfNeed(channelHandlerContext, object);

    }


    public void sendToConnector(Object object, String key) {

        if (this.acceptorChannelHandlerMap != null) {
            ChannelHandlerContext channelHandlerContext = this.acceptorChannelHandlerMap.getChannel(key);

            if (channelHandlerContext != null) {
                sendTo(object, channelHandlerContext);
            }
        }

    }


    public void sendToAcceptor(Object object) {

        if (this.connectorChannelHolder != null) {
            ChannelHandlerContext channelHandlerContext = this.connectorChannelHolder.getChannelHandlerContext();

            if (channelHandlerContext != null) {
                sendTo(object, channelHandlerContext);
            }
        }

    }

    public void sendTo(Object object, ChannelHandlerContext channelHandlerContext) {

        MessageLite messageLite = (MessageLite) object;
        // 连在同一个Gate

        // 发送
        channelHandlerContext.channel().writeAndFlush(messageLite);
        LOG.warn("sendToClient... {}", messageLite);
        // 需要ACK
        toBeAcknowledgedIfNeed(channelHandlerContext, object);
    }


    private void toBeAcknowledgedIfNeed(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageLite messageLite = (MessageLite) object;

        if (messageLite instanceof MessageTypeA.Message) {
            LOG.warn("messageLite... instanceof MessageTypeA.Message...");
            MessageTypeA.Message msg = (MessageTypeA.Message) messageLite;
            this.resendMessageMap.put(msg.getSeq(), new ResendMessage(messageLite, channelHandlerContext, msg.getSeq()));
        }

    }

}
