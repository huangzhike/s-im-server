package mmp.im.logic.database.dao;


import mmp.im.common.model.Group;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Component;


@Component
public interface GroupDao {


    @Insert("insert into group(name) values(#{name})")
    Integer addGroup(Group group);


    @Delete("delete from group where id=#{groupId}")
    Integer removeGroup(String groupId);

}

