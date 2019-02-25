package im.database.service;


import mmp.im.common.model.GateInfo;
import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

public class ServerService {

    private final String SERVER_DATABASE = "SERVER_STATUS_DATABASE_@_";


    public ServerService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private RedisUtil redisUtil;

    public Map<String, GateInfo> getGateList() {

        return redisUtil.getMap(SERVER_DATABASE, GateInfo.class);

    }

    public void updateGateList(GateInfo gateInfo) {

        redisUtil.addMapValue(SERVER_DATABASE, gateInfo.getServerId(), gateInfo);

    }


}
