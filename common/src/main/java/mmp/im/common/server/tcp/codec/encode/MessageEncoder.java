package mmp.im.common.server.tcp.codec.encode;


import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import mmp.im.common.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<MessageLite> {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
        LOG.warn("MessageEncoder encode start...");


        // TODO 加密消息体

        byte[] body = msg.toByteArray();
        byte[] header = encodeHeader(msg, (short) body.length);
        LOG.warn("MessageEncoder encode header... {}", header);
        LOG.warn("MessageEncoder encode body... {}", body);

        out.writeBytes(header);
        out.writeBytes(body);
        LOG.warn("MessageEncoder bodyLength...{}", body.length);

        //
        // ByteBuf buf = Unpooled.buffer(header.length + body.length);
        // buf.writeBytes(header);
        // buf.writeBytes(body);
        //
        // out.writeBytes(buf);

        LOG.warn("MessageEncoder encode finished...");
    }

    private byte[] encodeHeader(MessageLite msg, short bodyLength) {
        byte protocolType = 0x0f;

        if (msg instanceof ClientMessageBody.ClientMessage) {
            protocolType = ProtocolHeader.ProtocolType.MESSAGE.getType();
        } else if (msg instanceof AcknowledgeBody.Acknowledge) {
            protocolType = ProtocolHeader.ProtocolType.ACKNOWLEDGE.getType();
        } else if (msg instanceof HeartbeatBody.Heartbeat) {
            protocolType = ProtocolHeader.ProtocolType.HEART_BEAT.getType();
        } else if (msg instanceof ServerMessageBody.ServerMessage) {
            protocolType = ProtocolHeader.ProtocolType.SERVER.getType();
        } else if (msg instanceof ClientLoginBody.ClientLogin) {
            protocolType = ProtocolHeader.ProtocolType.CLIENT_LOGIN.getType();
        } else if (msg instanceof ClientLogoutBody.ClientLogout) {
            protocolType = ProtocolHeader.ProtocolType.CLIENT_LOGOUT.getType();
        }

        byte[] header = new byte[ProtocolHeader.HEAD_LENGTH];

        header[0] = (byte) ((ProtocolHeader.FLAG_NUM >> 24) & 0xff);
        header[1] = (byte) ((ProtocolHeader.FLAG_NUM >> 16) & 0xff);
        header[2] = (byte) ((ProtocolHeader.FLAG_NUM >> 8) & 0xff);
        header[3] = (byte) (ProtocolHeader.FLAG_NUM & 0xff);

        header[4] = protocolType;
        header[5] = (byte) ((bodyLength >> 8) & 0xff);

        header[6] = (byte) (bodyLength & 0xff);

        return header;

    }
}
