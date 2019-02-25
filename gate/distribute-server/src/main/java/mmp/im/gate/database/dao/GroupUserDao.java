package mmp.im.gate.database.dao;


import mmp.im.common.model.Group;
import mmp.im.common.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface GroupUserDao {


    @Select("select * from user left join group_user on group_user.groupId = #{groupId} and group_user.userId = user.id")
    List<User> getGroupUserList(String groupId);


    @Select("select userId from group_user where group_user.groupId = #{groupId}")
    List<String> getGroupUserIdList(String groupId);

    @Select("select * from group left join group_user on group_user.userId = #{userId} and group_user.groupId = group.id")
    List<Group> getUserGroupList(String userId);


    @Delete("delete from group_user where userId=#{userId} and groupId=#{groupId}")
    Integer quitGroup(String userId, String groupId);

    @Insert("insert into group_user(userId,groupId) values(#{userId},#{groupId})")
    Integer joinGroup(String userId, String groupId);

}
