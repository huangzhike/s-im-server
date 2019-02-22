package mmp.im.common.service.service;


import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SessionService {

    private final String RECENT_FRIEND_SESSION_DATABASE = "RECENT_FRIEND_SESSION_DATABASE_";
    private final String RECENT_GROUP_SESSION_DATABASE = "RECENT_GROUP_SESSION_DATABASE_";


    @Autowired
    private RedisUtil redisUtil;

    public void addRecentFriendSession(Long from, Long to) {

        redisUtil.addSet(RECENT_FRIEND_SESSION_DATABASE + from, String.valueOf(to));
        redisUtil.addSet(RECENT_FRIEND_SESSION_DATABASE + to, String.valueOf(from));

    }

    public Set<String> getRecentFriendSession(Long userId) {

        return redisUtil.getSet(RECENT_FRIEND_SESSION_DATABASE + userId);

    }

    public void addRecentGroupSession(Long userId, Long groupId) {

        redisUtil.addSet(RECENT_GROUP_SESSION_DATABASE + userId, String.valueOf(groupId));

    }

    public Set<String>  getRecentGroupSession(Long userId) {

     return    redisUtil.getSet(RECENT_GROUP_SESSION_DATABASE + userId);

    }


}
