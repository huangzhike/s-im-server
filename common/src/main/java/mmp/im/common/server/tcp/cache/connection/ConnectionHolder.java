package mmp.im.common.server.tcp.cache.connection;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionHolder {

    private static final ConcurrentHashMap<String, ChannelHandlerContext> clientConnectionMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, ChannelHandlerContext> serverConnectionMap = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHolder.class);

    public static ChannelHandlerContext getClientConnection(String key) {
        return clientConnectionMap.get(key);
    }

    public static ChannelHandlerContext getServerConnection(String key) {
        return serverConnectionMap.get(key);
    }

    public static List<ChannelHandlerContext> getServerConnectionList() {
        return (List<ChannelHandlerContext>) serverConnectionMap.values();
    }

    public static ChannelHandlerContext addClientConnection(String key, ChannelHandlerContext channelHandlerContext) {

        // 之后重复登录需要踢掉原来的连接
        if (clientConnectionMap.containsKey(key)) {
            LOG.warn("重复连接 key... {}", key);
            ChannelHandlerContext ctx = clientConnectionMap.remove(key);
            if (ctx.channel().isOpen()) {
                ctx.channel().close();
            }
        }
        return clientConnectionMap.putIfAbsent(key, channelHandlerContext);

    }

    public static ChannelHandlerContext addServerConnection(String key, ChannelHandlerContext channelHandlerContext) {

        // 之后重复登录需要踢掉原来的连接
        if (serverConnectionMap.containsKey(key)) {
            ChannelHandlerContext ctx = serverConnectionMap.remove(key);
            if (ctx.channel().isOpen()) {
                ctx.channel().close();
            }
        }
        return clientConnectionMap.putIfAbsent(key, channelHandlerContext);

    }

    public static ChannelHandlerContext removeClientConnection(String key) {
        return clientConnectionMap.remove(key);
    }

    public static ChannelHandlerContext removeServerConnection(String key) {
        return serverConnectionMap.remove(key);
    }
}
