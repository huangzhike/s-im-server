package mmp.im.common.util.bean;

import mmp.im.common.model.FriendMessage;
import mmp.im.common.model.GroupMessage;
import mmp.im.common.protocol.ProtobufMessage;

public class ProtobufToBeanUtil {
    public static FriendMessage toBean(ProtobufMessage.FriendMessage friendMessage) {
        FriendMessage message = new FriendMessage();
        return message;

    }
    public static GroupMessage toBean(ProtobufMessage.GroupMessage groupMessage) {
        GroupMessage message = new GroupMessage();
        return message;
    }
}
