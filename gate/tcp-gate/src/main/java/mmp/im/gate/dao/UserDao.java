package mmp.im.gate.dao;


import mmp.im.gate.model.User;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {

    User getUser(User user);
}

