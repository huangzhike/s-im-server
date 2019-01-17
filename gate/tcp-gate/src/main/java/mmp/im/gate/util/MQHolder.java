package mmp.im.gate.util;

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
