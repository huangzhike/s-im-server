package mmp.im.logic.service;

import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

@Service
public class GroupMessageService {

    /*
     * 暂时用redis 用hbase替换
     * */
    private final String GROUP_MESSAGE_DATABASE = "GROUP_MESSAGE_DATABASE_";
    private final String GROUP_MESSAGE_UNREAD_DATABASE = "GROUP_MESSAGE_UNREAD_DATABASE_";


    @Autowired
    private RedisUtil redisUtil;

    // 收到MQ推送的群消息持久化
    public void saveGroupMessage(GroupMessage groupMessage) {

        Long groupId = groupMessage.getTo();

        redisUtil.addSortedSet(GROUP_MESSAGE_DATABASE + groupId, groupMessage.getSeqId(), groupMessage);

    }

    // 获取群消息
    public List<GroupMessage> getGroupMessage(Long groupId, Long start, Long end) {
        return redisUtil.getSortedSet(GROUP_MESSAGE_DATABASE + groupId, start, end, GroupMessage.class);

    }


    // 群成员未读群消息，默认未读
    public void updateOfflineUserGroupMessage(String userId, String groupId, String lastReadMessageId) {

        redisUtil.hset(GROUP_MESSAGE_UNREAD_DATABASE + userId, groupId, lastReadMessageId);
    }


    public String getOfflineUserGroupMessage(String userId, String groupId) {

        return redisUtil.hget(GROUP_MESSAGE_UNREAD_DATABASE + userId, groupId);
    }

}
