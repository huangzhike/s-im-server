package mmp.im.logic.service;


import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final String RECENT_FRIEND_SESSION_DATABASE = "RECENT_FRIEND_SESSION_DATABASE_";
    private final String RECENT_GROUP_SESSION_DATABASE = "RECENT_GROUP_SESSION_DATABASE_";



    @Autowired
    private RedisUtil redisUtil;

    public void addRecentFriendSession(Long userId, String sessionId) {

        redisUtil.addSet(RECENT_FRIEND_SESSION_DATABASE + userId, sessionId);

    }

    public void getRecentFriendSession(Long userId) {

        redisUtil.getSet(RECENT_FRIEND_SESSION_DATABASE + userId);

    }

    public void addRecentGroupSession(Long userId, String sessionId) {

        redisUtil.addSet(RECENT_GROUP_SESSION_DATABASE + userId, sessionId);

    }

    public void getRecentGroupSession(Long userId) {

        redisUtil.getSet(RECENT_GROUP_SESSION_DATABASE + userId);

    }


}
