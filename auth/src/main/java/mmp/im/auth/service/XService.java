package mmp.im.auth.service;

import mmp.im.auth.dao.UserDao;
import mmp.im.auth.util.RedisUtil;
import mmp.im.common.model.Info;
import mmp.im.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class XService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisUtil redisUtil;


    private ConcurrentHashMap<String, List<Info>> userServerMap = new ConcurrentHashMap();

    public User getUser(User user) {
        return user;
    }


    public List<Info> getUserServerList(String key) {
        return userServerMap.get(key);
    }

    public void putUserServerList(String key, Info info) {
        List<Info> list = userServerMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
            userServerMap.putIfAbsent(key, list);
        }
        list.add(info);
    }

    public void removeUserServerList(String key, Info info) {
        List<Info> list = userServerMap.get(key);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Info i = (Info) iterator.next();
            if (i.getServerInfo().equals(info.getServerInfo())) {
                iterator.remove();
            }
        }
    }


    public <T> List<T> getUserServerList(User user) {

        return (List<T>) redisUtil.getMapValue("userServerMap", user.getId(), List.class);
    }

    public void putUserServerList(User user, Info info) {

        List<Info> list = (List<Info>) redisUtil.getMapValue("userServerMap", user.getId(), List.class);
        list.add(info);
        redisUtil.setMapValue("userServerMap", user.getId(), list);
    }

    public void removeUserServerList(User user, Info info) {
        List<Info> list = (List<Info>) redisUtil.getMapValue("userServerMap", user.getId(), List.class);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Info i = (Info) iterator.next();
            if (i.getServerInfo().equals(info.getServerInfo())) {
                iterator.remove();
            }
        }
        redisUtil.setMapValue("userServerMap", user.getId(), list);
    }


}
