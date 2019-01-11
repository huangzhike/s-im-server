package mmp.im.auth.service;

import mmp.im.auth.dao.UserDao;

import mmp.im.auth.model.Info;
import mmp.im.auth.model.User;
import mmp.im.auth.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;


@Service
public class XService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisUtil redisUtil;

    public User getUser(User user) {
        return user;
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
