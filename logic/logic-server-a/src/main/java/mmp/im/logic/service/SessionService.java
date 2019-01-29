package mmp.im.logic.service;


import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private RedisUtil redisUtil;


    private final String RECENT_FRIEND_SESSION_DATABASE = "RECENT_FRIEND_SESSION_DATABASE_";
    private final String RECENT_GROUP_SESSION_DATABASE = "RECENT_GROUP_SESSION_DATABASE_";

    public void addRecentFriendSession(Long userId, Long sessionId) {

        redisUtil.addList(RECENT_FRIEND_SESSION_DATABASE + userId, sessionId);

    }

}
