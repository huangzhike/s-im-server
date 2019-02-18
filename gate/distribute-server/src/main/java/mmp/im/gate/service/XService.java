package mmp.im.gate.service;


import mmp.im.gate.dao.XDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XService {

    @Autowired
    private XDao xDao;

    public List<Long> getUserFriendIdList(Long userId) {
        return xDao.getUserFriendIdList(userId);
    }

    public List<Long> getGroupUserIdList(Long groupId) {
        return xDao.getGroupUserIdList(groupId);
    }

}
