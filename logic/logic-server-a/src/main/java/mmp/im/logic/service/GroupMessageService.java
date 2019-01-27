package mmp.im.logic.service;


import mmp.im.common.model.GroupMessage;
import mmp.im.common.model.GroupReadMessage;
import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMessageService {

    @Autowired
    private XService xService;
    @Autowired
    private RedisUtil redisUtil;

    private final String GROUP_MESSAGE_DATABASE = "GROUP_MESSAGE_DATABASE";
    private final String GROUP_MESSAGE_UNREAD_DATABASE = "GROUP_MESSAGE_UNREAD_DATABASE";

    // 收到MQ推送的群消息持久化
    public void saveGroupMessage(GroupMessage groupMessage) {

        redisUtil.setMapValue(GROUP_MESSAGE_DATABASE, groupMessage.getTo(), groupMessage);
        List<String> userIdList = xService.getGroupUserIdList(groupMessage.getTo());


        for (String userId : userIdList) {

        }

    }

    // 群成员未读群消息，默认未读
    public void setOfflineUserGroupMessage(String userId, String groupId, String lastReadMessageId) {

        GroupReadMessage groupReadMessage = new GroupReadMessage();
        groupReadMessage.setGroupId(groupId);
        groupReadMessage.setLastReadMessageId(lastReadMessageId);
        redisUtil.setMapValue(GROUP_MESSAGE_UNREAD_DATABASE, userId, groupReadMessage);
    }

    public void removeOfflineUserGroupMessage(String userId, String groupId, String lastReadMessageId) {

    }

    public void getOfflineUserGroupMessage(String userId, String groupId, String lastReadMessageId) {

    }

}
