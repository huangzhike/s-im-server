package mmp.im.common.server.cache.acknowledge;

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

    private long lastSendTimeStamp = System.currentTimeMillis();

    public ResendMessage(long id, MessageLite msg, ChannelHandlerContext channelHandlerContext) {
        this.id = id;
        this.msg = msg;
        this.channelHandlerContext = channelHandlerContext;

    }


}
