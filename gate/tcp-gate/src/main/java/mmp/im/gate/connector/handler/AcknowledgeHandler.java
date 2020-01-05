package mmp.im.gate.connector.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.message.ResendMessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.Acknowledge;

public class AcknowledgeHandler  implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = Acknowledge.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();

        Acknowledge message = (Acknowledge) object;

        LOG.warn("Acknowledge {}", message);

        // 移除
        ResendMessageManager.getInstance().remove(message.getAck());


    }


}

