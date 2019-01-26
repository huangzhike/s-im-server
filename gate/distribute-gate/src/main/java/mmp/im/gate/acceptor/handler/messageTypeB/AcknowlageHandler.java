package mmp.im.gate.acceptor.handler.messageTypeB;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.MessageTypeB;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageMap;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcknowlageHandler implements IMessageTypeHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getHandlerName() {
        return String.valueOf(ProtocolHeader.ProtocolType.PROTOBUF_ACKNOWLEDGE.getType());
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        MessageTypeB.Acknowledge acknowledge = (MessageTypeB.Acknowledge) object;
        try {
            SpringContextHolder.getBean(ResendMessageMap.class).remove(acknowledge.getAck());
            LOG.warn("receive acknowledge... {}", acknowledge);
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }

    }
}

