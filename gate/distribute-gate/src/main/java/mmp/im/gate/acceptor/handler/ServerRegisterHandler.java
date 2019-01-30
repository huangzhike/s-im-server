package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;

import static mmp.im.common.protocol.ProtobufMessage.ServerRegister;

public class ServerRegisterHandler implements INettyMessageHandler {


    private final String name = ServerRegister.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {


        // TODO 重复接收

        ServerRegister message = (ServerRegister) object;
        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        String serverId = message.getSeverId();

        ContextHolder.getAcceptorChannelMap().addChannel(serverId, channelHandlerContext);

        channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(serverId);

        ReferenceCountUtil.release(object);

    }
}


