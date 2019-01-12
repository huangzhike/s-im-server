package mmp.im.gate.cache.connection;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionHolder {

    private static final ConcurrentHashMap<String, ChannelHandlerContext> connectionMap = new ConcurrentHashMap<>();


    public static ChannelHandlerContext getConnection(String key) {
        return connectionMap.get(key);
    }

    public static ChannelHandlerContext addConnection(String key,ChannelHandlerContext channelHandlerContext) {
        // 之后重复登录需要踢掉原来的连接
        return connectionMap.putIfAbsent(key, channelHandlerContext);
    }
    public static ChannelHandlerContext  removeConnection(String key){
       return connectionMap.remove(key);
    }
}
