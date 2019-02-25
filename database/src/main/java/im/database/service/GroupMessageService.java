package im.database.service;

import mmp.im.common.model.GroupMessage;
import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

public class GroupMessageService {

    /*
     * 暂时用redis 用hbase替换
     * */
    private final String GROUP_MESSAGE_DATABASE = "GROUP_MESSAGE_DATABASE_@_";

    private final String GROUP_MESSAGE_UNREAD_DATABASE = "GROUP_MESSAGE_UNREAD_DATABASE_@_";


    public GroupMessageService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    private RedisUtil redisUtil;

    // 收到MQ推送的群消息持久化
    public void saveGroupMessage(GroupMessage groupMessage) {

        String groupId = groupMessage.getTo();

        redisUtil.addSortedSetValue(GROUP_MESSAGE_DATABASE + groupId, groupMessage.getSeqId(), groupMessage);

    }

    // 获取群消息
    public Set<GroupMessage> getGroupMessageList(String groupId, Long start, Long end) {

        return redisUtil.getSortedSet(GROUP_MESSAGE_DATABASE + groupId, start, end, GroupMessage.class);

    }


    // 群成员未读群消息，默认未读
    public void updateReadUserGroupMessageId(String from, String to, Long lastReadMessageId) {

        redisUtil.hset(GROUP_MESSAGE_UNREAD_DATABASE + from, to, String.valueOf(lastReadMessageId));

    }


    public String getReadUserGroupMessageId(String from, String to) {

        return redisUtil.hget(GROUP_MESSAGE_UNREAD_DATABASE + from, to);

    }

}
