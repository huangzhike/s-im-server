package mmp.im.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class Heartbeat extends ProtocolHeader {

    private static final ByteBuf HEARTBEAT_BUF;

    static {
        ByteBuf buf = Unpooled.buffer(HEAD_LENGTH);
        buf.writeInt(FLAG_NUM);
        buf.writeByte(ProtocolType.HEART_BEAT.getType());
        buf.writeShort(0);
        HEARTBEAT_BUF = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(buf));
    }

    public static ByteBuf build() {
        return HEARTBEAT_BUF.duplicate();
    }
}
