package mmp.im.common.service.dao;


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
    List<User> getGroupUserList(Long groupId);


    @Select("select userId from group_user where group_user.groupId = #{groupId}")
    List<Long> getGroupUserIdList(Long groupId);

    @Select("select * from group left join group_user on group_user.userId = #{userId} and group_user.groupId = group.id")
    List<Group> getUserGroupList(Long userId);


    @Delete("delete from group_user where userId=#{userId} and groupId=#{groupId}")
    Integer quitGroup(Long userId, Long groupId);

    @Insert("insert into group_user(userId,groupId) values(#{userId},#{groupId})")
    Integer joinGroup(Long userId, Long groupId);

}
