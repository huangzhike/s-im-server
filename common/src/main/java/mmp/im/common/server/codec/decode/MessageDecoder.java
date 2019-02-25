package mmp.im.common.server.codec.decode;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.protocol.util.ProtocolHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Data
@Accessors(chain = true)
public class MessageDecoder extends ByteToMessageDecoder {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private ProtocolParserHolder protocolParserHolder = new ProtocolParserHolder();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        LOG.warn("decode start...");

        in.markReaderIndex(); // 重用ByteBuf 已读 可读 可写 剩余

        // 如果可读长度小于包头长度，退出
        if (in.readableBytes() < ProtocolHeader.HEAD_LENGTH) {
            return;
        }

        // 读取消息，读的过程中，readIndex的指针也在移动
        int flag = in.readInt();
        // 标记头不对
        if (flag != ProtocolHeader.FLAG_NUM) {
            // 重置指针
            in.resetReaderIndex();
            LOG.warn("标记头不对...");

            channelHandlerContext.close();
            LOG.warn("非法数据，关闭连接...");
            return;
        }

        // 获取包头中的类型
        byte commandId = in.readByte();

        // 获取包头中的body长度，高低位，大端模式
        byte high = in.readByte();
        byte low = in.readByte();

        short bodyLength = (short) ((low & 0xff) | ((high & 0xff) << 8));
        // int bodyLength = in.readUnsignedShort();
        // 如果可读长度小于body长度，恢复读指针，退出
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            LOG.warn("可读长度不对... 小于body长度...");
            return;
        }

        LOG.warn("decode bodyLength...{}", bodyLength);

        ByteBuf frame = in.readBytes(bodyLength);


        int readableLen = frame.readableBytes(); // 获取可读取字节数量
        byte[] array;

        int offset;

        if (frame.hasArray()) {
            // 堆缓冲区(基于数组实现)，通过hasArray判断是否支持数组
            array = frame.array();
            offset = frame.arrayOffset() + frame.readerIndex();
        } else {
            // 直接缓冲区
            array = new byte[readableLen];
            frame.getBytes(frame.readerIndex(), array, 0, readableLen);
            offset = 0;
        }

        // byte[] bytes = new byte[bodyLength];
        //
        // in.readBytes(bytes);


        IProtocolParser protocolParser = protocolParserHolder.get(commandId);
        if (protocolParser != null) {
            MessageLite msg = (MessageLite) protocolParser.parse(array);
            out.add(msg);
            LOG.warn("decode finished...");
        } else {
            LOG.warn("无法识别，通道关闭");
        }


    }


}
