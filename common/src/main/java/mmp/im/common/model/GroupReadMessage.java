package mmp.im.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GroupReadMessage {
    private String groupId;
    private String lastReadMessageId;
}
