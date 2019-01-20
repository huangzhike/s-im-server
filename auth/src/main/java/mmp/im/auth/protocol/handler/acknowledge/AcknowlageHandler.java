package mmp.im.auth.protocol.handler.acknowledge;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.AcknowledgeBody.Acknowledge;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.server.tcp.cache.ack.ResendMessageMap;

public class AcknowlageHandler implements IMessageTypeHandler {

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

