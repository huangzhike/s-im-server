package mmp.im.gate.connection;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final ConcurrentHashMap<String, ConnectionWrapper> connectionMap = new ConcurrentHashMap<>();


    public static ConnectionWrapper getConnection(String id) {
        return connectionMap.get(id);
    }

    public static ConnectionWrapper addConnection(ConnectionWrapper connectionWrapper) {
        // 之后重复登录需要踢掉原来的连接
        return connectionMap.putIfAbsent(connectionWrapper.getUserId(), connectionWrapper);
    }
}
