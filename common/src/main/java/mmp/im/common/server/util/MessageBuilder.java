package mmp.im.common.server.util;

import com.google.protobuf.MessageLite;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static mmp.im.common.protocol.ProtobufMessage.*;

public class MessageBuilder {


    private static final AtomicLong seqGenerator = new AtomicLong();

    public static MessageLite buildHeartbeat() {
        return Heartbeat.newBuilder().build();
    }

    public static MessageLite buildAcknowledge(Long seq) {
        return Acknowledge.newBuilder().setAck(seq).build();
    }


    ///////////
    public static FriendMessage buildFriendMessage(String from, String to, String content, String type) {

        FriendMessage.Builder builder = FriendMessage.newBuilder();
        builder.setSeq(seqGenerator.getAndIncrement());
        builder.setFrom(from);
        builder.setTo(to);
        builder.setContent(content);
        builder.setType(type);
        return builder.build();
    }


    public static FriendMessage buildTransFriendMessage(FriendMessage friendMessage) {

        FriendMessage.Builder builder = friendMessage.toBuilder();
        builder.setSeq(seqGenerator.getAndIncrement());

        return builder.build();
    }

    public static FriendMessage buildTransFriendMessage(FriendMessage friendMessage, Long seqId) {

        FriendMessage.Builder builder = friendMessage.toBuilder();
        builder.setSeqId(seqId);
        builder.setSeq(seqGenerator.getAndIncrement());


        return builder.build();
    }

    ///////////


    public static GroupMessage buildGroupMessage(String from, String to, String content, String type) {

        GroupMessage.Builder builder = GroupMessage.newBuilder();
        builder.setSeq(seqGenerator.getAndIncrement());
        builder.setFrom(from);
        builder.setTo(to);
        builder.setContent(content);
        builder.setType(type);
        return builder.build();
    }


    public static GroupMessage buildTransGroupMessage(GroupMessage groupMessage) {

        GroupMessage.Builder builder = groupMessage.toBuilder();
        builder.setSeq(seqGenerator.getAndIncrement());

        return builder.build();
    }

    public static GroupMessage buildTransGroupMessage(GroupMessage groupMessage, List<String> broadcastIdList, Long seqId) {

        GroupMessage.Builder builder = groupMessage.toBuilder();
        builder.setSeqId(seqId);
        builder.setSeq(seqGenerator.getAndIncrement());
        if (broadcastIdList != null) {
            builder.addAllBroadcastIdList(broadcastIdList);
        }

        return builder.build();
    }


    //////////////

    public static ClientStatus buildClientStatus(String userId, String serverId, Boolean status, String clientInfo) {

        ClientStatus.Builder builder = ClientStatus.newBuilder();
        builder.setUserId(userId);
        builder.setServerId(serverId);
        builder.setStatus(status);
        builder.setClientInfo(clientInfo);
        builder.setSeq(seqGenerator.getAndIncrement());

        return builder.build();
    }

    public static ClientStatus buildTransClientStatus(ClientStatus clientStatus) {

        ClientStatus.Builder builder = clientStatus.toBuilder();
        builder.setSeq(seqGenerator.getAndIncrement());
        return builder.build();
    }

    public static ClientStatus buildTransClientStatus(ClientStatus clientStatus, List<String> broadcastIdList) {

        ClientStatus.Builder builder = clientStatus.toBuilder();

        builder.setSeq(seqGenerator.getAndIncrement());
        if (broadcastIdList != null) {
            builder.addAllBroadcastIdList(broadcastIdList);
        }

        return builder.build();
    }


    // ////////////
//
    public static ServerRegister buildServerRegister(String serverId, String token) {
        ServerRegister.Builder builder = ServerRegister.newBuilder();
        builder.setSeverId(serverId);
        builder.setToken(token);
        builder.setSeq(seqGenerator.getAndIncrement());
        return builder.build();
    }


    public static ClientLogin buildClientLogin(String userId, String token, String clientInfo) {

        ClientLogin.Builder builder = ClientLogin.newBuilder();
        builder.setUserId(userId);
        builder.setToken(token);
        builder.setClientInfo(clientInfo);
        builder.setSeq(seqGenerator.getAndIncrement());
        return builder.build();
    }


    public static ClientLogout buildClientLogout(String userId, String token, String clientInfo) {
        ClientLogout.Builder builder = ClientLogout.newBuilder();
        builder.setUserId(userId);
        builder.setToken(token);
        builder.setClientInfo(clientInfo);
        builder.setSeq(seqGenerator.getAndIncrement());
        return builder.build();
    }

///////////////

    public static ReadMessage buildReadMessage(String from, String to, Long seqId) {

        ReadMessage.Builder builder = ReadMessage.newBuilder();
        builder.setFrom(from);
        builder.setTo(to);
        builder.setSeqId(seqId);

        return builder.build();
    }

    public static ReadMessage buildTransReadMessage(ReadMessage readMessage) {

        ReadMessage.Builder builder = readMessage.toBuilder();

        return builder.build();
    }


}
