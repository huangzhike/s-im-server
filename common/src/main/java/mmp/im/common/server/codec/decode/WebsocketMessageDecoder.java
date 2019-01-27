package mmp.im.common.server.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class WebsocketMessageDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {

        // 文本帧处理(收到的消息广播到前台客户端)
        if (frame instanceof TextWebSocketFrame) {

        }

        if (frame instanceof BinaryWebSocketFrame) {

            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            byte[] by = new byte[frame.content().readableBytes()];
            binaryWebSocketFrame.content().readBytes(by);
            ByteBuf bytebuf = Unpooled.buffer();
            bytebuf.writeBytes(by);
            out.add(bytebuf);
        }


    }
}
