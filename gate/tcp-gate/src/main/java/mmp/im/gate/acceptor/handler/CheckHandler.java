package mmp.im.gate.acceptor.handler;

import io.netty.channel.Channel;
import mmp.im.common.server.util.AttributeKeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CheckHandler {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected boolean login(Channel channel) {
        return channel.attr(AttributeKeyHolder.CHANNEL_ID).get() != null;
    }

    protected boolean duplicate(Channel channel, Long seq) {
        return channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().contains(seq);
    }
}
