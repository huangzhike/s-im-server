package mmp.im.common.protocol;

public abstract class ProtocolHeader {

    public static final int FLAG_NUM = 0xbabe;
    public static final int HEAD_LENGTH = 4 + 1 + 2;

    protected int flag = FLAG_NUM;

    protected byte protocolType;

    protected short bodyLength;

    public enum ProtocolType {
        HEARTBEAT((byte) 0x01),
        ACKNOWLEDGE((byte) 0x02),
        MESSAGE((byte) 0x03);

        private byte type;

        ProtocolType(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }

        public ProtocolType setType(byte type) {
            this.type = type;
            return this;
        }
    }
}
