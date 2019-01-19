package mmp.im.gate.util;

import mmp.im.server.tcp.MQPublisher;

public final class MQHolder {

    private static final MQPublisher mq;

    static {
        mq = new MQPublisher("", "");
        mq.start();
    }

    public static MQPublisher getMq() {
        return mq;
    }
}
