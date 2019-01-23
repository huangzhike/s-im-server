package mmp.im.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


@Data
@Accessors(chain = true)
public class Group {

    private String id;
    private String name;
    private List<User> groupUser;

}
