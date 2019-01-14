package mmp.im.gate.util;

import com.google.protobuf.MessageLite;
import com.rabbitmq.client.MessageProperties;
import mmp.im.common.util.mq.MQ;
import mmp.im.protocol.*;

import java.nio.ByteBuffer;

public class MQPublisher extends MQ {

    public MQPublisher(String mqURI, String publishToQueue, String consumeFromQueue) {
        super(mqURI, publishToQueue, consumeFromQueue);
    }

    @Override
    protected boolean process(byte[] contentBody) {

        return true;
    }

    @Override
    public boolean publish(String exchangeName, String routingKey, Object msg) {
        boolean ok = false;

        MessageLite messageLite = (MessageLite) msg;

        try {
            pubChannel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, encode(messageLite));
            ok = true;
        } catch (Exception e) {
            resendQueue.add(new ResendElement(exchangeName, routingKey, msg));
        }
        return ok;
    }

    private byte[] encode(MessageLite msg) {
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
        }

        byte[] body = msg.toByteArray();

        ByteBuffer buffer = ByteBuffer.allocate(body.length + 1); // 声明一个缓冲区
        buffer.put(protocolType);
        buffer.put(body);

        return buffer.array();
    }
}

