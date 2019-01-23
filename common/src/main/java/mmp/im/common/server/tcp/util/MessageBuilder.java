package mmp.im.common.server.tcp.util;

import com.google.protobuf.Any;
import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.MessageTypeB;
import mmp.im.common.protocol.MessageTypeC;

public class MessageBuilder {


    public MessageLite buildHeartbeat() {
        return MessageTypeC.Heartbeat.newBuilder().build();
    }

    public MessageLite buildAcknowlege(Long seq) {
        return MessageTypeB.Acknowledge.newBuilder().setAck(seq).build();
    }

    public MessageLite buildMessage() {

        MessageTypeA.Message.Builder builder = MessageTypeA.Message.newBuilder();

        builder.setSeq(SeqGenerator.get());
        builder.setFrom("from mmp");
        builder.setTo("to mmp");
        builder.setType(MessageTypeA.Message.Type.FRIEND_MESSAGE);

        MessageTypeA.Message.FriendMessage.Builder msgBuilder = MessageTypeA.Message.FriendMessage.newBuilder();

        msgBuilder.setData("尼玛的 为什么");

        builder.setData(Any.pack(msgBuilder.build()));

        MessageTypeA.Message result = builder.build();

        return result;
    }
}
