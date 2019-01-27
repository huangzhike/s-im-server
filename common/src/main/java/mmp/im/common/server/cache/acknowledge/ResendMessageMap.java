package mmp.im.common.server.cache.acknowledge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResendMessageMap {

    private final Map<Long, ResendMessage> messageToAckMap = new ConcurrentHashMap<>();

    public ResendMessage get(Long key) {
        return messageToAckMap.get(key);
    }

    public ResendMessage remove(Long key) {
        return messageToAckMap.remove(key);
    }

    public ResendMessage put(Long key, ResendMessage resendMessage) {
        return messageToAckMap.put(key, resendMessage);
    }

    public Map<Long, ResendMessage> getMap() {
        return messageToAckMap;
    }

}
