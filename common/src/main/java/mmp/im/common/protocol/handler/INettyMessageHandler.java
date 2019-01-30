package mmp.im.common.protocol.handler;


import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;

public interface INettyMessageHandler {

    String getHandlerName();

    void process(ChannelHandlerContext channelHandlerContext, MessageLite messageLite);

}
