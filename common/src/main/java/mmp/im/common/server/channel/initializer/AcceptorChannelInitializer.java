package mmp.im.common.server.channel.initializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class AcceptorChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ChannelHandler[] channelHandler;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(
                channelHandler
        );
    }

}
