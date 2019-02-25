package im.database.service;


import mmp.im.common.model.Info;
import mmp.im.common.model.User;
import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

public class StatusService {

    private final String USER_SERVER_STATUS_DATABASE = "USER_SERVER_STATUS_DATABASE_@_";

    public StatusService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private RedisUtil redisUtil;


    public Set<String> getUserServerList(String userId) {
        return redisUtil.getSet(USER_SERVER_STATUS_DATABASE + userId, String.class);
    }

    public void putUserServerList(String userId, String serverId) {
        redisUtil.addSetValue(USER_SERVER_STATUS_DATABASE + userId, serverId);
    }

    public void removeUserServerList(String userId, String serverId) {
        redisUtil.removeSetValue(USER_SERVER_STATUS_DATABASE + userId, serverId);
    }


}
