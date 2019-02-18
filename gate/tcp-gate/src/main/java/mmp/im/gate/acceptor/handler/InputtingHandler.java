package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.Inputting;

public class InputtingHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = Inputting.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();

        if (!this.login(channel)) {
            LOG.warn("未登录");
        }

        Inputting message = (Inputting) object;

        LOG.warn("ReadMessage... {}", message);

        // 生成消息待转发
        Inputting m = MessageBuilder.buildTransInputting(message);

        // distribute
        ContextHolder.getMessageSender().sendToAcceptor(m);

        ReferenceCountUtil.release(object);
    }
}

