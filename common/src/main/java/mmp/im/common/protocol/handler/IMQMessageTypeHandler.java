package mmp.im.common.protocol.handler;

public interface IMQMessageTypeHandler {

    String getHandlerName();

    void process(Object object);
}
