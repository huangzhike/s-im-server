package mmp.im.common.server.tcp.handler.channel;

import io.netty.channel.ChannelHandler;

/**
 * 方便ChannelPipeline的handler的初始化和在重连的时候获取所有的handlers
 */
public interface IChannelHandlerHolder {

    ChannelHandler[] handlers();

}
