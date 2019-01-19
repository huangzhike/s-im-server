package mmp.im.gate.service;

import mmp.im.gate.dao.UserDao;
import mmp.im.gate.model.Group;
import mmp.im.gate.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class XService {

    @Autowired
    private UserDao userDao;

    public User getUser(User user) {
        return user;
    }

    public List<User> getGroupUserList(Group group) {
        return null;
    }


}
