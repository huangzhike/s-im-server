package mmp.im.auth.database.service;

import mmp.im.auth.database.dao.FriendDao;
import mmp.im.auth.database.dao.GroupDao;
import mmp.im.auth.database.dao.GroupUserDao;
import mmp.im.auth.database.dao.UserDao;
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


    public List<String>  getUserFriendIdList(String userId) {
        return friendDao.getUserFriendIdList(userId);
    }

    public List<User> getFriendList(String userId) {
        return friendDao.getFriendList(userId);
    }

    public Integer addFriend(String idFrom, String idTo) {
        return friendDao.addFriend(idFrom, idTo);
    }

    public Integer removeFriend(String idFrom, String idTo) {
        return friendDao.removeFriend(idFrom, idTo);
    }

    ///////////
    public Integer addGroup(Group group) {
        return groupDao.addGroup(group);
    }

    public Integer removeGroup(String groupId) {
        return groupDao.removeGroup(groupId);
    }


    ///////////
    public List<User> getGroupUserList(String groupId) {
        return groupUserDao.getGroupUserList(groupId);
    }

    public List<String> getGroupUserIdList(String groupId) {
        return groupUserDao.getGroupUserIdList(groupId);
    }

    public List<Group> getUserGroupList(String userId) {
        return groupUserDao.getUserGroupList(userId);
    }

    public Integer quitGroup(String userId, String groupId) {
        return groupUserDao.quitGroup(userId, groupId);
    }

    public Integer joinGroup(String userId, String groupId) {
        return groupUserDao.joinGroup(userId, groupId);
    }

    ///////////
    public User getUser(User user) {
        return userDao.getUser(user.getId());
    }

    public User getUser(String userId) {
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
