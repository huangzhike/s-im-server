package mmp.im.auth.database.dao;

import mmp.im.common.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface FriendDao {

    @Select("select * from user left join friend on " +
            "friend.idFrom = #{userId} or friend.idTo = #{userId} " +
            "where user.id = friend.idTo or user.id = friend.idFrom")
    List<User> getFriendList(String userId);

    @Select("select id from user left join friend on " +
            "friend.idFrom = #{userId} or friend.idTo = #{userId} " +
            "where user.id = friend.idTo or user.id = friend.idFrom")
    List<String> getUserFriendIdList(String userId);



    @Insert("insert into friend(idFrom,idTo) values(#{idFrom},#{idTo})")
    Integer addFriend(String idFrom, String idTo);


    @Delete("delete from friend where (idFrom=#{idFrom} and idTo=#{idTo}) or (idFrom=#{idTo} and idTo=#{idFrom})")
    Integer removeFriend(String idFrom, String idTo);


}
