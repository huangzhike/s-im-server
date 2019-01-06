package mmp.im.gate.ack;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;


public class MessageToAck {

    private final long id;

    private final MessageLite msg;

    private final Channel channel;
    private long timestamp = System.currentTimeMillis();

    public MessageToAck(MessageLite msg, Channel channel, long id) {
        this.msg = msg;
        this.channel = channel;

        this.id = id;
    }

    public long getId() {
        return id;
    }

    public MessageLite getMsg() {
        return msg;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
