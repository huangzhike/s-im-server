package mmp.im.logic.dao;


import mmp.im.common.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {

    // @Select("select * from user where id = #{id}")
    // User getUser(User user);

    @Select("select * from user where id = #{userId}")
    User getUser(String userId);

    @Insert("insert into user(name,password) values(#{name},#{password})")
    Integer addUser(User user);

    @Update("update user set name=#{name},password=#{password} where id=#{id})")
    Integer updateUser(User user);

    @Delete("delete from user where id=#{id}")
    Integer removeUser(User user);
}
