package mmp.im.common.model;

import java.util.Date;

public class Info {
    private String token;
    private String protocolInfo;
    private String clientInfo;
    private String serverInfo;
    private Date lastLogin;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProtocolInfo() {
        return protocolInfo;
    }

    public void setProtocolInfo(String protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
