package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;

import static mmp.im.common.protocol.ProtobufMessage.Heartbeat;

public class HeartbeatHandler implements INettyMessageHandler {

    private final String name = Heartbeat.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Heartbeat message = (Heartbeat) object;
        ReferenceCountUtil.release(object);
    }
}

