package mmp.im.common.server.codec.encode;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class WebsocketMessageEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
        ByteBuf result = null;
        if (msg instanceof MessageLite) {
            result = wrappedBuffer(((MessageLite) msg).toByteArray());
        }
        if (msg instanceof MessageLite.Builder) {
            result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
        }
        // 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的
        WebSocketFrame frame = new BinaryWebSocketFrame(result);
        out.add(frame);
    }
}
