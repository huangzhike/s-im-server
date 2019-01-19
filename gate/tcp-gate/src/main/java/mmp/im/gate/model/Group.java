package mmp.im.gate.model;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private List<User> groupUser;

    public String getId() {
        return id;
    }

    public Group setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public List<User> getGroupUser() {
        return groupUser;
    }

    public Group setGroupUser(List<User> groupUser) {
        this.groupUser = groupUser;
        return this;
    }
}
