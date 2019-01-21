package mmp.im.auth.protocol.handler.acknowledge;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.AcknowledgeBody.Acknowledge;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.ack.ResendMessageMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcknowlageHandler implements IMessageTypeHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public String getHandlerName() {

        return String.valueOf(ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType());
    }

    @Override
    public void process(ChannelHandlerContext channel, Object object) {
        Acknowledge acknowledge = (Acknowledge) object;
        try {

            ResendMessageMap.remove(acknowledge.getAck());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

