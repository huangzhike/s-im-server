package mmp.im.common.server.tcp.util;

import com.google.protobuf.Any;
import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.MessageTypeA;
import mmp.im.common.protocol.MessageTypeB;
import mmp.im.common.protocol.MessageTypeC;

public class MessageBuilder {


    public static MessageLite buildHeartbeat() {
        return MessageTypeC.Heartbeat.newBuilder().build();
    }

    public static MessageLite buildAcknowlege(Long seq) {
        return MessageTypeB.Acknowledge.newBuilder().setAck(seq).build();
    }


    public static MessageLite buildClientLoginStatus(String from, String to, String data) {

        MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.FRIEND_MESSAGE);

        MessageTypeA.Message.FriendMessage.Builder msgBuilder = MessageTypeA.Message.FriendMessage.newBuilder();

        msgBuilder.setData(data);

        builder.setData(Any.pack(msgBuilder.build()));

        return builder.build();
    }

    private static MessageTypeA.Message.Builder buildMessage(String from, String to, MessageTypeA.Message.Type type) {

        MessageTypeA.Message.Builder builder = MessageTypeA.Message.newBuilder();

        builder.setSeq(SeqGenerator.get());
        builder.setFrom(from);
        builder.setTo(to);
        builder.setType(type);


        return builder;


    }
}
