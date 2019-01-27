package mmp.im.common.server.event;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Event {

    private final EventType type;
    private final String remoteAddr;
    private final Channel channel;

    public Event(EventType type, String remoteAddr, Channel channel) {
        this.type = type;
        this.remoteAddr = remoteAddr;
        this.channel = channel;
    }


}
