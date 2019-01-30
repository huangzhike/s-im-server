package mmp.im.common.server.channel.initializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class ConnectorChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private ChannelHandler[] channelHandler;

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        channel.pipeline().addLast(
                channelHandler
        );
    }
}
