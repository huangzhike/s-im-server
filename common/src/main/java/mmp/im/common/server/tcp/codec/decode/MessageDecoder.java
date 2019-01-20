package mmp.im.common.server.tcp.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.ProtocolHeader;

import java.util.List;


public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 如果可读长度小于包头长度，退出
        while (in.readableBytes() > ProtocolHeader.HEAD_LENGTH) {

            in.markReaderIndex();

            int flag = in.readInt();
            // 标记头不对
            if (flag != ProtocolHeader.FLAG_NUM) {
                in.resetReaderIndex();
                return;
            }

            // 获取包头中的类型
            byte protocolType = in.readByte();


            // 获取包头中的body长度，高低位，大端模式
            byte high = in.readByte();
            byte low = in.readByte();

            short bodyLength = (short) ((low & 0xff) | ((high & 0xff) << 8));


            // 如果可读长度小于body长度，恢复读指针，退出
            if (in.readableBytes() < bodyLength) {
                in.resetReaderIndex();
                return;
            }

            // 读取body
            ByteBuf bodyByteBuf = in.readBytes(bodyLength);


            int readableLen = bodyByteBuf.readableBytes(); // 获取可读取字节数量
            byte[] array;

            int offset;

            if (bodyByteBuf.hasArray()) {
                // 堆缓冲区(基于数组实现)，通过hasArray判断是否支持数组
                array = bodyByteBuf.array();
                offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
            } else {
                // 直接缓冲区
                array = new byte[readableLen];
                bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                offset = 0;
            }

            // ByteBuf clientMessage = Unpooled.buffer(bodyLength);
            // clientMessage.writeBytes(array);
            // out.add(clientMessage.array());

            byte[] bytes = new byte[bodyLength];

            in.readBytes(bytes);


            out.add(new ParserPacket().setProtocolType(protocolType).setBody(bytes));


        }
    }


}
