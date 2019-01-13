package mmp.im.logic.service;

import mmp.im.logic.dao.UserDao;
import mmp.im.logic.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class XService {

    @Autowired
    private UserDao userDao;

    public User getUser(User user) {
        return user;
    }



}
