package mmp.im.common.server.util;

import com.google.protobuf.Any;
import com.google.protobuf.MessageLite;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MessageBuilder {


    private static final AtomicLong seqGenerator = new AtomicLong();

//     public static MessageLite buildHeartbeat() {
//         return MessageTypeC.Heartbeat.newBuilder().build();
//     }
//
//     public static MessageLite buildAcknowledge(Long seq) {
//         return MessageTypeB.Acknowledge.newBuilder().setAck(seq).build();
//     }
//
//
//     ///////////
//     public static MessageLite buildFriendMessage(String from, String to, String data) {
//
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.FRIEND_MESSAGE);
//
//         MessageTypeA.Message.FriendMessage.Builder msgBuilder = MessageTypeA.Message.FriendMessage.newBuilder();
//
//         msgBuilder.setData(data);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//     public static MessageLite buildFriendMessage(String from, String to, MessageTypeA.Message.FriendMessage data) {
//
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.FRIEND_MESSAGE);
//
//         builder.setData(Any.pack(data));
//
//         return builder.build();
//     }
//
//
//     public static MessageLite buildBroadcastFriendMessage(String from, String to, String data) {
//
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.BROADCAST_FRIEND_MESSAGE);
//
//         MessageTypeA.Message.FriendMessage.Builder msgBuilder = MessageTypeA.Message.FriendMessage.newBuilder();
//
//         msgBuilder.setData(data);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//
//     public static MessageLite buildBroadcastFriendMessage(String from, String to, MessageTypeA.Message.FriendMessage data) {
//
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.BROADCAST_FRIEND_MESSAGE);
//
//         builder.setData(Any.pack(data));
//
//         return builder.build();
//     }
//     ///////////
//
//
//     public static MessageLite buildGroupMessage(String from, String to, String data) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.GROUP_MESSAGE);
//
//         MessageTypeA.Message.GroupMessage.Builder msgBuilder = MessageTypeA.Message.GroupMessage.newBuilder();
//
//         msgBuilder.setData(data);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//     public static MessageLite buildGroupMessage(String from, String to, MessageTypeA.Message.GroupMessage data) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.GROUP_MESSAGE);
//
//
//         builder.setData(Any.pack(data));
//
//         return builder.build();
//     }
//
//     public static MessageLite buildBroadcastGroupMessage(String from, String to, String data, List<String> idList) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.BROADCAST_GROUP_MESSAGE);
//
//         MessageTypeA.Message.GroupMessage.Builder msgBuilder = MessageTypeA.Message.GroupMessage.newBuilder();
//
//         msgBuilder.setData(data);
//
//         builder.addAllNotifyIdList(idList);
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//     public static MessageLite buildBroadcastGroupMessage(String from, String to, MessageTypeA.Message.GroupMessage data, List<String> idList) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.BROADCAST_GROUP_MESSAGE);
//
//
//         builder.addAllNotifyIdList(idList);
//         builder.setData(Any.pack(data));
//
//         return builder.build();
//     }
//
//     ////////////////
//
//     public static MessageLite buildClientStatus(String from, String to, String id, String serverId, Boolean status) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.CLIENT_STATUS);
//
//         MessageTypeA.Message.ClientStatus.Builder msgBuilder = MessageTypeA.Message.ClientStatus.newBuilder();
//
//         msgBuilder.setId(id);
//         msgBuilder.setServerId(serverId);
//         msgBuilder.setStatus(status);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//     public static MessageLite buildClientStatus(String from, String to, MessageTypeA.Message.ClientStatus data) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.CLIENT_STATUS);
//
//
//         builder.setData(Any.pack(data));
//
//         return builder.build();
//     }
//
//     public static MessageLite buildBroadcastClientStatus(String from, String to, String id, String serverId, Boolean status, List<String> idList) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.BROADCAST_CLIENT_STATUS);
//
//         MessageTypeA.Message.ClientStatus.Builder msgBuilder = MessageTypeA.Message.ClientStatus.newBuilder();
//
//         msgBuilder.setId(id);
//         msgBuilder.setServerId(serverId);
//         msgBuilder.setStatus(status);
//
//         builder.addAllNotifyIdList(idList);
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//
//     public static MessageLite buildBroadcastClientStatus(String from, String to, MessageTypeA.Message.ClientStatus data, List<String> idList) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.BROADCAST_CLIENT_STATUS);
//
//         builder.addAllNotifyIdList(idList);
//         builder.setData(Any.pack(data));
//
//         return builder.build();
//     }
//
// ////////////
//
//     public static MessageLite buildServerRegister(String from, String to, String id, String data, String token) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.SERVER_REGISTER);
//
//         MessageTypeA.Message.ServerRegister.Builder msgBuilder = MessageTypeA.Message.ServerRegister.newBuilder();
//
//         msgBuilder.setId(id);
//         msgBuilder.setData(data);
//         msgBuilder.setToken(token);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//
//     public static MessageLite buildClientLogin(String from, String to, String id, String token) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.CLIENT_LOGIN);
//
//         MessageTypeA.Message.ClientLogin.Builder msgBuilder = MessageTypeA.Message.ClientLogin.newBuilder();
//
//         msgBuilder.setId(id);
//
//         msgBuilder.setToken(token);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//
//     public static MessageLite buildClientLogout(String from, String to, String id, String token) {
//         MessageTypeA.Message.Builder builder = buildMessage(from, to, MessageTypeA.Message.Type.CLIENT_LOGOUT);
//
//         MessageTypeA.Message.ClientLogout.Builder msgBuilder = MessageTypeA.Message.ClientLogout.newBuilder();
//
//         msgBuilder.setId(id);
//
//         msgBuilder.setToken(token);
//
//         builder.setData(Any.pack(msgBuilder.build()));
//
//         return builder.build();
//     }
//
//
//     private static MessageTypeA.Message.Builder buildMessage(String from, String to, MessageTypeA.Message.Type type) {
//
//         MessageTypeA.Message.Builder builder = MessageTypeA.Message.newBuilder();
//
//         builder.setSeq(seqGenerator.getAndIncrement());
//         builder.setFrom(from);
//         builder.setTo(to);
//         builder.setType(type);
//
//         return builder;
//     }
}
