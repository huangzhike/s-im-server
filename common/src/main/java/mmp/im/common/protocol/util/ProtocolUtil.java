package mmp.im.common.protocol.util;

import static mmp.im.common.protocol.ProtobufMessage.*;

public class ProtocolUtil {

    public static final int PROTOBUF_HEARTBEAT = 1;
    public static final int PROTOBUF_ACKKNOWLEDGE = 2;
    public static final int PROTOBUF_FRIEND_MESSAGE = 3;
    public static final int PROTOBUF_GROUP_MESSAGE = 4;
    public static final int PROTOBUF_SERVER_REGISTER = 5;
    public static final int PROTOBUF_CLIENT_LOGIN = 6;
    public static final int PROTOBUF_CLIENT_LOGOUT = 7;
    public static final int PROTOBUF_CLIENT_STATUS = 8;
    public static final int PROTOBUF_READ_MESSAGE = 9;
    public static final int PROTOBUF_INPUTTING = 10;
    public static final int PROTOBUF_APPLY_FRIEND = 11;


    public static byte encodeCommandId(Object msg) {
        byte commandId = 0x00;

        if (msg instanceof Heartbeat) {
            commandId = PROTOBUF_HEARTBEAT;
        } else if (msg instanceof Acknowledge) {
            commandId = PROTOBUF_ACKKNOWLEDGE;
        } else if (msg instanceof FriendMessage) {
            commandId = PROTOBUF_FRIEND_MESSAGE;
        } else if (msg instanceof GroupMessage) {
            commandId = PROTOBUF_GROUP_MESSAGE;
        } else if (msg instanceof ServerRegister) {
            commandId = PROTOBUF_SERVER_REGISTER;
        } else if (msg instanceof ClientLogin) {
            commandId = PROTOBUF_CLIENT_LOGIN;
        } else if (msg instanceof ClientStatus) {
            commandId = PROTOBUF_CLIENT_STATUS;
        } else if (msg instanceof ReadMessage) {
            commandId = PROTOBUF_READ_MESSAGE;
        } else if (msg instanceof Inputting) {
            commandId = PROTOBUF_INPUTTING;
        } else if (msg instanceof ApplyFriend) {
            commandId = PROTOBUF_APPLY_FRIEND;
        }
        return commandId;
    }
}
