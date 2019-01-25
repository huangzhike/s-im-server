package mmp.im.common.protocol.util;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.*;

public class ProtocolUtil {

    public static byte encodeProtocolType(MessageLite msg) {
        byte protocolType = 0;

        if (msg instanceof MessageTypeA.Message) {
            protocolType = ProtocolHeader.ProtocolType.PROTOBUF_MESSAGE.getType();
        } else if (msg instanceof MessageTypeB.Acknowledge) {
            protocolType = ProtocolHeader.ProtocolType.PROTOBUF_ACKNOWLEDGE.getType();
        } else if (msg instanceof MessageTypeC.Heartbeat) {
            protocolType = ProtocolHeader.ProtocolType.PROTOBUF_HEARTBEAT.getType();
        } else if (msg instanceof MessageTypeD.MessageLite) {
            protocolType = ProtocolHeader.ProtocolType.PROTOBUF_MESSAGE_LITE.getType();
        }
        return protocolType;
    }
}
