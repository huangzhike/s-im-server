package mmp.im.gate.acceptor.handler.messageTypeC;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeC;
import mmp.im.common.protocol.util.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatHandler implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {
        return String.valueOf(ProtocolHeader.ProtocolType.PROTOBUF_HEARTBEAT.getType());
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageTypeC.Heartbeat heartbeat = (MessageTypeC.Heartbeat) object;
        try {
            LOG.warn("receive heartbeat... {}", heartbeat);
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }

    }
}

