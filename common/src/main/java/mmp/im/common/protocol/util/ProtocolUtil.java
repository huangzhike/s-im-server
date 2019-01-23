package mmp.im.common.protocol.util;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.MessageTypeB;
import mmp.im.common.protocol.MessageTypeC;
import mmp.im.common.protocol.ProtocolHeader;

public class ProtocolUtil {

    public static byte encodeProtocolType(MessageLite msg) {
        byte protocolType = 0;

        if (msg instanceof MessageTypeA.Message) {
            protocolType = ProtocolHeader.ProtocolType.MESSAGE.getType();
        } else if (msg instanceof MessageTypeB.Acknowledge) {
            protocolType = ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType();
        } else if (msg instanceof MessageTypeC.Heartbeat) {
            protocolType = ProtocolHeader.ProtocolType.HEARTBEAT.getType();
        }
        return protocolType;
    }
}
