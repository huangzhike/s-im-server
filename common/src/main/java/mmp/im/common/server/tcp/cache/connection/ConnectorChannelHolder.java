package mmp.im.common.server.tcp.cache.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class ConnectorChannelHolder {

    private ChannelHandlerContext channelHandlerContext;

}
