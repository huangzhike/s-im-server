package mmp.im.gate.codec.encode;


import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import mmp.im.protocol.*;


@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {

        byte[] body = msg.toByteArray();
        byte[] header = encodeHeader(msg, (short) body.length);

        out.writeBytes(header);
        out.writeBytes(body);

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
