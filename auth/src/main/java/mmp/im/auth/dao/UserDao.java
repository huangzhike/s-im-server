package mmp.im.auth.dao;


import mmp.im.auth.model.User;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {

    User getUser(User user);
}

