package mmp.im.gate.ack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageToAckMap {

    private static final Map<Long, MessageToAck> messageToAckMap = new ConcurrentHashMap<>();

    public static MessageToAck get(Long key) {
        return messageToAckMap.get(key);
    }

    public static Object remove(Long key) {
        return messageToAckMap.remove(key);
    }

    public static Object put(Long key, MessageToAck messageToAck) {
        return messageToAckMap.put(key, messageToAck);
    }

    public static Map<Long, MessageToAck> getMap() {
        return messageToAckMap;
    }

}
