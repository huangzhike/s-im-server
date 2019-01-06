package mmp.im.gate.event;

import io.netty.channel.Channel;

public class Event {

    private final EventType type;
    private final String remoteAddr;
    private final Channel channel;

    public Event(EventType type, String remoteAddr, Channel channel) {
        this.type = type;
        this.remoteAddr = remoteAddr;
        this.channel = channel;
    }

    public EventType getType() {
        return type;
    }


    public String getRemoteAddr() {
        return remoteAddr;
    }


    public Channel getChannel() {
        return channel;
    }


    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", channel=" + channel +
                '}';
    }
}
