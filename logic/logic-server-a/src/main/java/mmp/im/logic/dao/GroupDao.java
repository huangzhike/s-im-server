package mmp.im.logic.dao;


import mmp.im.common.model.User;
import org.springframework.stereotype.Component;

@Component
public interface GroupDao {

    User getUser(User user);
}

