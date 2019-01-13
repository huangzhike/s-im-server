package mmp.im.logic.dao;


import mmp.im.logic.model.User;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {

    User getUser(User user);
}

