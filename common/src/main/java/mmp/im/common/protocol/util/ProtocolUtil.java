package mmp.im.common.protocol.util;

import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.*;

public class ProtocolUtil {

    public static byte encodeProtocolType(MessageLite msg) {
        byte protocolType = 0x0f;

        if (msg instanceof ClientMessageBody.ClientMessage) {
            protocolType = ProtocolHeader.ProtocolType.MESSAGE.getType();
        } else if (msg instanceof AcknowledgeBody.Acknowledge) {
            protocolType = ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType();
        } else if (msg instanceof HeartbeatBody.Heartbeat) {
            protocolType = ProtocolHeader.ProtocolType.HEART_BEAT.getType();
        } else if (msg instanceof ServerMessageBody.ServerMessage) {
            protocolType = ProtocolHeader.ProtocolType.SERVER.getType();
        } else if (msg instanceof ClientLoginBody.ClientLogin) {
            protocolType = ProtocolHeader.ProtocolType.CLIENT_LOGIN.getType();
        } else if (msg instanceof ClientLogoutBody.ClientLogout) {
            protocolType = ProtocolHeader.ProtocolType.CLIENT_LOGOUT.getType();
        }
        return protocolType;
    }
}
