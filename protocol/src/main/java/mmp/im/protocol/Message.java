package mmp.im.protocol;

import java.util.concurrent.atomic.AtomicLong;

public final class Message extends ProtocolHeader {


    private static final AtomicLong sequenceGenerator = new AtomicLong(0);

    private Object body;

    public Message() {

    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
