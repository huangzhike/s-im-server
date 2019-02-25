package mmp.im.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GateInfo {

    private String ip;
    private int port;
    private String serverId;

    private TYPE type;

    private boolean status;


    public enum TYPE {

        TCP(), WEBSOCKET();

        TYPE() {
        }
    }
}
