package mmp.im.common.protocol.handler;

import com.google.protobuf.MessageLite;

public interface IMessageHandler {

    String getHandlerName();

    void process(MessageLite messageLite);


}
