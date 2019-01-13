package mmp.im.gate.protocol.handler.acknowledge;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.ack.ResendMessageMap;
import mmp.im.gate.protocol.handler.IMessageTypeHandler;
import mmp.im.protocol.AcknowledgeBody.Acknowledge;
import mmp.im.protocol.ProtocolHeader;

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

