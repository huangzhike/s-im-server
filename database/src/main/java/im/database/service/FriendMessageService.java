package im.database.service;


import mmp.im.common.model.FriendMessage;
import mmp.im.common.util.redis.RedisUtil;
import mmp.im.common.util.session.SessionUtil;

import java.util.Set;

public class FriendMessageService {

    /*
     * 暂时用redis 用hbase替换
     * */
    private final String FRIEND_MESSAGE_DATABASE = "FRIEND_MESSAGE_DATABASE_@_";

    private final String FRIEND_MESSAGE_UNREAD_DATABASE = "FRIEND_MESSAGE_UNREAD_DATABASE_@_";

    public FriendMessageService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private RedisUtil redisUtil;

    // 收到MQ推送的好友消息持久化
    public void saveFriendMessage(FriendMessage groupMessage) {

        String sessionId = SessionUtil.getSessionId(groupMessage.getTo(), groupMessage.getFrom());

        redisUtil.addSortedSetValue(FRIEND_MESSAGE_DATABASE + sessionId, groupMessage.getSeqId(), groupMessage);

    }

    public Set<FriendMessage> getFriendMessageList(String sessionId, Long start, Long end) {

        return redisUtil.getSortedSet(FRIEND_MESSAGE_DATABASE + sessionId, start, end, FriendMessage.class);

    }

    // 已读会话
    public void updateReadUserFriendMessageId(String from, String to, Long lastReadMessageId) {

        redisUtil.hset(FRIEND_MESSAGE_UNREAD_DATABASE + from, to, String.valueOf(lastReadMessageId));

    }


    public String getReadUserFriendMessageId(String from, String to) {

        return redisUtil.hget(FRIEND_MESSAGE_UNREAD_DATABASE + from, to);

    }


}
