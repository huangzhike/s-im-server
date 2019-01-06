package mmp.im.protocol;

public final class Acknowledge extends ProtocolHeader {

    private long ack;

    public Acknowledge(long ack){
        this.ack = ack;
    }

    public long getAck() {
        return ack;
    }

    public Acknowledge setAck(long ack) {
        this.ack = ack;
        return this;
    }

    @Override
    public String toString() {
        return "Acknowledge{" +
                "ack=" + ack +
                ", flag=" + flag +
                ", protocolType=" + protocolType +
                ", bodyLength=" + bodyLength +
                '}';
    }
}
