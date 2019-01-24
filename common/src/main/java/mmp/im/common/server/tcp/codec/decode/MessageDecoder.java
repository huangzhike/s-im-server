package mmp.im.common.server.tcp.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.ProtocolHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class MessageDecoder extends ByteToMessageDecoder {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

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

            ctx.close();
            LOG.warn("非法数据，关闭连接...");
            return;
        }

        // 获取包头中的类型
        byte protocolType = in.readByte();

        // 获取包头中的body长度，高低位，大端模式
        byte high = in.readByte();
        byte low = in.readByte();

        short bodyLength = (short) ((low & 0xff) | ((high & 0xff) << 8));
        // int bodyLength = in.readUnsignedShort();
        // 如果可读长度小于body长度，恢复读指针，退出
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            LOG.warn("可读长度不对...小于body长度...");
            return;
        }

        LOG.warn("decode bodyLength...{}", bodyLength);

        // // 读取body
        // byte[] bytes = new byte[in.readableBytes()];
        // in.readBytes(bytes);
        // // 读取body
        // ByteBuf frame = Unpooled.buffer(bodyLength);
        // in.readBytes(frame);

        // 读取body
        ByteBuf bodyByteBuf = in.readBytes(bodyLength);

        int readableLen = bodyByteBuf.readableBytes(); // 获取可读取字节数量
        byte[] array;
        int offset;

        if (bodyByteBuf.hasArray()) {
            LOG.warn("堆缓冲区...");
            // 堆缓冲区(基于数组实现)，通过hasArray判断是否支持数组
            array = bodyByteBuf.array();
            LOG.warn("堆缓冲区... array... {}", array);
            // offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
        } else {
            // 直接缓冲区
            LOG.warn("直接缓冲区...");
            array = new byte[readableLen];
            bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
            LOG.warn("直接缓冲区... array... {}", array);
            // offset = 0;
        }

        out.add(new ParserPacket().setProtocolType(protocolType).setBody(array));
        LOG.warn("decode finished...");

    }


}
