package mmp.im.common.service.service;

import mmp.im.common.service.dao.FriendDao;
import mmp.im.common.service.dao.GroupDao;
import mmp.im.common.service.dao.GroupUserDao;
import mmp.im.common.service.dao.UserDao;
import mmp.im.common.model.Group;
import mmp.im.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class XService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private GroupUserDao groupUserDao;


    public List<User> getFriendList(Long userId) {
        return friendDao.getFriendList(userId);
    }

    public Integer addFriend(Long idFrom, Long idTo) {
        return friendDao.addFriend(idFrom, idTo);
    }

    public Integer removeFriend(Long idFrom, Long idTo) {
        return friendDao.removeFriend(idFrom, idTo);
    }

    public Integer addGroup(Group group) {
        return groupDao.addGroup(group);
    }

    public Integer removeGroup(Long groupId) {
        return groupDao.removeGroup(groupId);
    }

    public List<User> getGroupUserList(Long groupId) {
        return groupUserDao.getGroupUserList(groupId);
    }

    public List<Long> getGroupUserIdList(Long groupId) {
        return groupUserDao.getGroupUserIdList(groupId);
    }

    public List<Group> getUserGroupList(Long userId) {
        return groupUserDao.getUserGroupList(userId);
    }

    public Integer quitGroup(Long userId, Long groupId) {
        return groupUserDao.quitGroup(userId, groupId);
    }

    public Integer joinGroup(Long userId, Long groupId) {
        return groupUserDao.joinGroup(userId, groupId);
    }



    public User getUser(User user) {
        return userDao.getUser(user.getId());
    }

    public User getUser(Long userId) {
        return userDao.getUser(userId);
    }

    public Integer addUser(User user) {
        return userDao.addUser(user);
    }

    public Integer updateUser(User user) {
        return userDao.updateUser(user);
    }

    public Integer removeUser(User user) {
        return userDao.removeUser(user);
    }

}
