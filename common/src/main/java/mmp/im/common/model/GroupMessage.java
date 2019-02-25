package mmp.im.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@Accessors(chain = true)
public class GroupMessage {
    private Long seqId;
    private String id;
    private String from;
    private String to;
    private String type;
    private Date time;
    private String data;
}
