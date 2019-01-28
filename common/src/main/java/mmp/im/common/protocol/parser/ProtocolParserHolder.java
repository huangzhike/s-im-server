package mmp.im.common.protocol.parser;

import mmp.im.common.protocol.util.ProtocolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static mmp.im.common.protocol.ProtobufMessage.*;


public class ProtocolParserHolder {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<Integer, IProtocolParser> parsers = new ConcurrentHashMap<>();

    public ProtocolParserHolder() {

        parsers.put(ProtocolUtil.PROTOBUF_ACKKNOWLEDGE, (bytes) -> {
            Acknowledge msg = null;
            try {
                msg = Acknowledge.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_HEARTBEAT, (bytes) -> {
            Heartbeat msg = null;
            try {
                msg = Heartbeat.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });
        parsers.put(ProtocolUtil.PROTOBUF_CLIENT_LOGOUT, (bytes) -> {
            ClientLogout msg = null;
            try {
                msg = ClientLogout.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_CLINENT_LOGIN, (bytes) -> {
            ClientLogin msg = null;
            try {
                msg = ClientLogin.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_APPLY_FRIEND, (bytes) -> {
            ApplyFriend msg = null;
            try {
                msg = ApplyFriend.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_CLIENT_STATUS, (bytes) -> {
            ClientStatus msg = null;
            try {
                msg = ClientStatus.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });
        parsers.put(ProtocolUtil.PROTOBUF_FRIEND_MESSAGE, (bytes) -> {
            FriendMessage msg = null;
            try {
                msg = FriendMessage.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_GROUP_MESSAGE, (bytes) -> {
            GroupMessage msg = null;
            try {
                msg = GroupMessage.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_INPUTTING, (bytes) -> {
            Inputting msg = null;
            try {
                msg = Inputting.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });
        parsers.put(ProtocolUtil.PROTOBUF_READ_MESSAGE, (bytes) -> {
            ReadMessage msg = null;
            try {
                msg = ReadMessage.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });

        parsers.put(ProtocolUtil.PROTOBUF_SERVER_REGISTER, (bytes) -> {
            ServerRegister msg = null;
            try {
                msg = ServerRegister.parseFrom(bytes);
            } catch (Exception e) {
                LOG.error("parseFrom Exception... {}", e);
            }
            return msg;
        });


        LOG.warn("parsers... {}", parsers);
        LOG.warn("parsers size... {}", parsers.size());
    }


    private Map<Integer, IProtocolParser> getParsers() {

        return parsers;
    }


    public IProtocolParser get(int commandId) {
        return getParsers().get(commandId);
    }


}
