package mmp.im.common.util.bean;

import mmp.im.common.model.FriendMessage;
import mmp.im.common.model.GroupMessage;
import mmp.im.common.protocol.ProtobufMessage;

import java.util.Date;

public class ProtobufToBeanUtil {
    public static FriendMessage toBean(ProtobufMessage.FriendMessage friendMessage) {
        FriendMessage message = new FriendMessage();
        message.setSeqId(friendMessage.getSeqId());
        message.setFrom(friendMessage.getFrom());
        message.setTo(friendMessage.getTo());
        message.setType(friendMessage.getType());
        message.setContent(friendMessage.getContent());
        message.setTime(new Date());
        return message;

    }
    public static GroupMessage toBean(ProtobufMessage.GroupMessage groupMessage) {
        GroupMessage message = new GroupMessage();
        message.setSeqId(groupMessage.getSeqId());
        message.setFrom(groupMessage.getFrom());
        message.setTo(groupMessage.getTo());
        message.setType(groupMessage.getType());
        message.setContent(groupMessage.getContent());
        message.setTime(new Date());
        return message;
    }
}
