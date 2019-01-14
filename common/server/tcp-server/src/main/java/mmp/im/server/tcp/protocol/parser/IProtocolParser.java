package mmp.im.server.tcp.protocol.parser;


import io.netty.channel.ChannelHandlerContext;

public interface IProtocolParser {

    int getProtocolKind();

    void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes);

}
