package mmp.im.gate.util;

import mmp.im.common.server.tcp.MQPublisher;

public final class MQHolder {

    private static final MQPublisher publisher;

    static {
        publisher = new MQPublisher("", "");
        publisher.start();
    }

    public static MQPublisher getMq() {
        return publisher;
    }
}
