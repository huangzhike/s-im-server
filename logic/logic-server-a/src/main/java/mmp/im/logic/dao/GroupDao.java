package mmp.im.logic.dao;


import mmp.im.common.model.Group;
import mmp.im.common.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupDao {


    @Insert("insert into group(name) values(#{name})")
    Integer addGroup(Group group);


    @Delete("delete from group where id=#{groupId}")
    Integer removeGroup(String groupId);


}

