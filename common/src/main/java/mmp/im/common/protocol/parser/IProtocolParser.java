package mmp.im.common.protocol.parser;

import io.netty.channel.ChannelHandlerContext;

public interface IProtocolParser {

    int getProtocolKind();

    void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes);

}
