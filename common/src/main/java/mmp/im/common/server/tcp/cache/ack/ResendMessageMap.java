package mmp.im.common.server.tcp.cache.ack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResendMessageMap {

    private static final Map<Long, ResendMessage> messageToAckMap = new ConcurrentHashMap<>();

    public static ResendMessage get(Long key) {
        return messageToAckMap.get(key);
    }

    public static Object remove(Long key) {
        return messageToAckMap.remove(key);
    }

    public static Object put(Long key, ResendMessage resendMessage) {
        return messageToAckMap.put(key, resendMessage);
    }

    public static Map<Long, ResendMessage> getMap() {
        return messageToAckMap;
    }

}
