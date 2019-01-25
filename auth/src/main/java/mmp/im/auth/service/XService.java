package mmp.im.auth.service;

import mmp.im.auth.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class XService {

    @Autowired
    private UserDao userDao;


}
