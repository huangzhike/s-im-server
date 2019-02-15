package mmp.im.gate.acceptor.handler;

import io.netty.channel.Channel;
import mmp.im.common.server.util.AttributeKeyHolder;


public abstract class CheckHandler {


    protected boolean login(Channel channel) {
        return channel.attr(AttributeKeyHolder.CHANNEL_ID).get() != null;
    }

    protected boolean duplicate(Channel channel, Long seq) {
        return channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().contains(seq);
    }
}
