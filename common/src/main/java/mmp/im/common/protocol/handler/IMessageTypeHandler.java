package mmp.im.common.protocol.handler;


import io.netty.channel.ChannelHandlerContext;

public interface IMessageTypeHandler {

    String getHandlerName();

    void process(ChannelHandlerContext channelHandlerContext, Object object);

}
