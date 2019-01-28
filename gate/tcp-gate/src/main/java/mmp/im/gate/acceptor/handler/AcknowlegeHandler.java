package mmp.im.gate.acceptor.handler;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.Acknowledge;

public class AcknowlegeHandler implements IMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = Acknowledge.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {
        Acknowledge acknowledge = (Acknowledge) object;
        SpringContextHolder.getBean(ResendMessageMap.class).remove(acknowledge.getAck());
        LOG.warn("receive acknowledge... {}", acknowledge);

    }
}

