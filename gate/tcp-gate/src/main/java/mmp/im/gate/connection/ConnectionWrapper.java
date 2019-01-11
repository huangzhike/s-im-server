package mmp.im.gate.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ConnectionWrapper {

    private String userId;
    private String gateWayId;
    private ChannelHandlerContext channelHandlerContext;

    public static AttributeKey<String> GATEWAY_ID = AttributeKey.valueOf("GATEWAY_ID");
    public static AttributeKey<String> USER_ID = AttributeKey.valueOf("GATEWAY_ID");

    ConnectionWrapper(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        this.userId = channelHandlerContext.attr(USER_ID).get();
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGateWayId() {
        return gateWayId;
    }

    public void setGateWayId(String gateWayId) {
        this.gateWayId = gateWayId;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
