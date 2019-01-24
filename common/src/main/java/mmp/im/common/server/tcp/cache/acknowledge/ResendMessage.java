package mmp.im.common.server.tcp.cache.acknowledge;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResendMessage {

    private final long id;

    private final MessageLite msg;

    private final ChannelHandlerContext channelHandlerContext;

    private long timestamp = System.currentTimeMillis();

    public ResendMessage(MessageLite msg, ChannelHandlerContext channelHandlerContext, long id) {
        this.msg = msg;
        this.channelHandlerContext = channelHandlerContext;
        this.id = id;
    }


}
