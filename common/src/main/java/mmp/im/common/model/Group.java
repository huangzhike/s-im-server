package mmp.im.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;


@Data
@Accessors(chain = true)
public class Group {

    private String id;
    private String name;
    private Date createTime;
    private String ownerId;
    private String remark;
    private List<User> groupUserList;
    private List<String> groupUserIdList;

}
