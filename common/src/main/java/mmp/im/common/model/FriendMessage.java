package mmp.im.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class FriendMessage {
    private String id;
    private Long seqId;
    private String from;
    private String to;
    private String type;
    private Date time;
    private String content;
    private Boolean read;
}
