package mmp.im.common.protocol;

public abstract class ProtocolHeader {

    public static final int FLAG_NUM = 0xbabe;
    public static final int HEAD_LENGTH = 4 + 1 + 2;

    protected int flag = FLAG_NUM;

    protected byte protocolType;

    protected short bodyLength;

    public enum ProtocolType {

        PROTOBUF_HEARTBEAT((byte) 0x01),
        PROTOBUF_ACKNOWLEDGE((byte) 0x02),
        PROTOBUF_MESSAGE((byte) 0x03),
        PROTOBUF_MESSAGE_LITE((byte) 0x04);

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
