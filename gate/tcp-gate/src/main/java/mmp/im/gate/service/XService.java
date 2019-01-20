package mmp.im.gate.service;

import mmp.im.common.model.Group;
import mmp.im.common.model.User;
import mmp.im.gate.dao.GroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class XService {

    @Autowired
    private GroupDao groupDao;

    public User getUser(User user) {
        return user;
    }

    public List<User> getGroupUserList(Group group) {
        return null;
    }


}
