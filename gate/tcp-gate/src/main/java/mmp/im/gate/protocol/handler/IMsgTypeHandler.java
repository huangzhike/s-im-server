package mmp.im.gate.protocol.handler;


import io.netty.channel.ChannelHandlerContext;

public interface IMsgTypeHandler {

    String getName();

    void process(ChannelHandlerContext channelHandlerContext, Object object);
}
