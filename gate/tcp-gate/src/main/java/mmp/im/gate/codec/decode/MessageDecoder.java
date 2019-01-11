package mmp.im.gate.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import mmp.im.protocol.Acknowledge;
import mmp.im.protocol.MessageBody;
import mmp.im.protocol.ProtocolHeader;

import java.nio.ByteBuffer;
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

            short bodyLength = (byte) ((low & 0xff) | ((high & 0xff) << 8));


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

            ByteBuf message = Unpooled.buffer(ProtocolHeader.HEAD_LENGTH - 4 - 2 + bodyLength);

            // 参考 https://www.jianshu.com/p/8e407689c15a

            // message.writeInt(flag);
            message.writeByte(protocolType);
            // message.writeShort(bodyLength);
            message.writeBytes(array);
            // // 要不要搞个对象池
            // Message message = new Message();
            //
            // // 反序列化
            // Object result = decodeBody(protocolType, array);
            // message.setBody(result);
            out.add(message.array());
        }
    }

    private Object decodeBody(byte protocolType, byte[] array) throws Exception {
        Object object = null;
        if (protocolType == ProtocolHeader.ProtocolType.MESSAGE.getType()) {
            object = MessageBody.Msg.parseFrom(array);
        } else if (protocolType == ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType()) {
            // ACK
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.put(array, 0, array.length);
            buffer.flip();

            object = new Acknowledge(buffer.getLong());
        } else if (protocolType == ProtocolHeader.ProtocolType.HEART_BEAT.getType()) {
            // 心跳
        }
        return object; // or throw exception
    }

}
