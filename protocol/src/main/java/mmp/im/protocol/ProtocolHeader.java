package mmp.im.protocol;

public abstract class ProtocolHeader {

    public static final int FLAG_NUM = 0xbabe;
    public static final int HEAD_LENGTH = 4 + 1 + 2;

    protected int flag = FLAG_NUM;

    protected byte protocolType;

    protected short bodyLength;

    public enum ProtocolType {
        HEART_BEAT((byte) 0x00), ACKNOWLEDGE((byte) 0x01), MESSAGE((byte) 0x02), SERVER((byte) 0x03);
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
