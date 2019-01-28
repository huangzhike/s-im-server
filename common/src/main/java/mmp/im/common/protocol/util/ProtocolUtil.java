package mmp.im.common.protocol.util;

import com.google.protobuf.MessageLite;

public class ProtocolUtil {

    public static final int PROTOBUF_HEARTBEAT=1;
    public static final int PROTOBUF_ACKKNOWLEDGE=2;
    public static final int PROTOBUF_FRIEND_MESSAGE=3;
    public static final int PROTOBUF_GROUP_MESSAGE=4;
    public static final int PROTOBUF_SERVER_REGISTER=5;
    public static final int PROTOBUF_CLINENT_LOGIN=6;
    public static final int PROTOBUF_CLIENT_LOGOUT=7;
    public static final int PROTOBUF_CLIENT_STATUS=8;
    public static final int PROTOBUF_READ_MESSAGE=9;
    public static final int PROTOBUF_INPUTTING=10;
    public static final int PROTOBUF_APPLY_FRIEND=11;


    public static byte encodeProtocolType(Object msg) {
        byte protocolType = 0x00;

        // if (msg instanceof MessageLite) {
        //     protocolType = ProtocolHeader.ProtocolType.PROTOBUF.getType();
        // }
        return protocolType;
    }
}
