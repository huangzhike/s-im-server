package mmp.im.gate.util.mq;

public final class MQHolder {

    private static final MQ mq ;

    static {
        mq = new MQ("", "", "");
        mq.start();
    }

    public static MQ getMq() {
        return mq;
    }
}
