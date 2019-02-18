package mmp.im.common.protocol.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;

public interface IMessageHandler {

    String getHandlerName();

    void process(MessageLite messageLite);


}
