package mmp.im.gate.dao;


import mmp.im.common.model.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface XDao {


    @Select("select id from user left join friend on " +
            "friend.idFrom = #{userId} or friend.idTo = #{userId} " +
            "where user.id = friend.idTo or user.id = friend.idFrom")
    List<Long> getUserFriendIdList(Long userId);


    @Select("select userId from group_user where group_user.groupId = #{groupId}")
    List<Long> getGroupUserIdList(Long groupId);


}
