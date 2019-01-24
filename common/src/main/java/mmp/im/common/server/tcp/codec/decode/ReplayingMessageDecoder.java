package mmp.im.common.server.tcp.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.ProtocolHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReplayingMessageDecoder extends ReplayingDecoder<ReplayingMessageDecoder.State> {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private byte protocolType;
    private short bodyLength;
    public ReplayingMessageDecoder() {
        super(ReplayingMessageDecoder.State.FLAG);
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (this.state()) {
            case FLAG:
                if (ProtocolHeader.FLAG_NUM != in.readInt()) {
                    throw new IllegalArgumentException();
                }
                LOG.warn("checkpoint -> PROTOCOL_TYPE");
                checkpoint(ReplayingMessageDecoder.State.PROTOCOL_TYPE);
                // 这里没有break，接下来进入PROTOCOL_TYPE代码块
            case PROTOCOL_TYPE:
                protocolType = in.readByte(); // 消息标志位
                LOG.warn("checkpoint -> BODY_LENGTH");
                checkpoint(ReplayingMessageDecoder.State.BODY_LENGTH);
            case BODY_LENGTH:
                // 获取包头中的body长度，高低位，大端模式
                byte high = in.readByte();
                byte low = in.readByte();

                bodyLength = (short) ((low & 0xff) | ((high & 0xff) << 8)); // 消息体长度
                LOG.warn("checkpoint -> BODY");
                checkpoint(ReplayingMessageDecoder.State.BODY);
            case BODY:
                ParserPacket parserPacket = new ParserPacket();

                byte[] bytes = new byte[bodyLength];

                in.readBytes(bytes);
                parserPacket.setProtocolType(protocolType).setBody(bytes);
                out.add(parserPacket);
                LOG.warn("checkpoint -> FLAG");
                checkpoint(ReplayingMessageDecoder.State.FLAG);
        }
    }


    /*
     * ReplayingDecoder这里的ByteBuf in的类型是ReplayingDecoderByteBuf
     * 当调用其read方法时，如果接受的数据长度小于期待read的数据长度，会抛出一个REPLAY异常
     * ReplayingDecoder会接收到这个异常后会将ByteBuf的读指针回退到checkpoint的位置
     * 通过checkpoint(State state)方法可以记录下当前的状态以及读指针的位置
     * 通过state()方法可以取得当前的状态，以助于跳转到相应代码的位置
     * */

    enum State {
        FLAG, PROTOCOL_TYPE, BODY_LENGTH, BODY
    }

}

