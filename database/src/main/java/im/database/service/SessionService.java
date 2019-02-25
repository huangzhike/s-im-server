package im.database.service;


import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

public class SessionService {

    private final String RECENT_FRIEND_SESSION_DATABASE = "RECENT_FRIEND_SESSION_DATABASE_@_";

    private final String RECENT_GROUP_SESSION_DATABASE = "RECENT_GROUP_SESSION_DATABASE_@_";


    public SessionService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private RedisUtil redisUtil;

    public void addUserRecentFriendSession(String from, String to) {

        redisUtil.addSetValue(RECENT_FRIEND_SESSION_DATABASE + from, to);

        redisUtil.addSetValue(RECENT_FRIEND_SESSION_DATABASE + to, from);

    }

    public Set<String> getUserRecentFriendSession(String userId) {

        return redisUtil.getSet(RECENT_FRIEND_SESSION_DATABASE + userId,String.class);

    }

    public void addUserRecentGroupSession(String userId, String groupId) {

        redisUtil.addSetValue(RECENT_GROUP_SESSION_DATABASE + userId,groupId);

    }

    public Set<String> getUserRecentGroupSession(String userId) {

        return redisUtil.getSet(RECENT_GROUP_SESSION_DATABASE + userId,String.class);

    }


}
