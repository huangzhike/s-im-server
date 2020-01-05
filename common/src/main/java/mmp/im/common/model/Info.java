package mmp.im.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@Accessors(chain = true)
public class Info {

    private String token;

    private String protocolInfo;

    private String clientInfo;

    private String serverId;

    private Date lastLogin = new Date();

}
