package mmp.im.logic.service;


import mmp.im.common.util.redis.RedisUtil;
import mmp.im.logic.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;

@Service
public class FriendMessageService {


    /*
     * 暂时用redis 用hbase替换
     * */
    private final String FRIEND_MESSAGE_DATABASE = "FRIEND_MESSAGE_DATABASE_";
    private final String FRIEND_MESSAGE_UNREAD_DATABASE = "FRIEND_MESSAGE_UNREAD_DATABASE_";

    @Autowired
    private RedisUtil redisUtil;

    // 收到MQ推送的好友消息持久化
    public void saveFriendMessage(FriendMessage groupMessage) {

        String sessionId = SessionUtil.getSessionId(groupMessage.getTo(),groupMessage.getFrom());

        redisUtil.addSortedSet(FRIEND_MESSAGE_DATABASE + sessionId, groupMessage.getSeqId(), groupMessage);

    }

    public List<FriendMessage> getFriendMessage(Long userId, Long start, Long end) {
        return redisUtil.getSortedSet(FRIEND_MESSAGE_DATABASE + userId, start, end, FriendMessage.class);

    }

    // 已读会话
    public void updateOfflineUserFriendMessage(String userId, String sessionId, String lastReadMessageId) {

        redisUtil.hset(FRIEND_MESSAGE_UNREAD_DATABASE + userId, sessionId, lastReadMessageId);
    }


    public String getOfflineUserFriendMessage(String userId, String sessionId) {

        return redisUtil.hget(FRIEND_MESSAGE_UNREAD_DATABASE + userId, sessionId);
    }


}
