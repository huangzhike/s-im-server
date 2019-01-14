package mmp.im.gate.util;

import mmp.im.common.util.mq.MQ;

public final class MQHolder {

    private static final MQPublisher mq;

    static {
        mq = new MQPublisher("", "", "");
        mq.start();
    }

    public static MQ getMq() {
        return mq;
    }
}
