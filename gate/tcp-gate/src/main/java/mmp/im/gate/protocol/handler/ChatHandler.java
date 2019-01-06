package mmp.im.gate.protocol.handler;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.ack.MessageToAck;
import mmp.im.gate.ack.MessageToAckMap;
import mmp.im.protocol.MessageBody.Msg;

public class ChatHandler implements IMsgTypeHandler {

    public String getName() {

        return String.valueOf(Msg.MsgType.MESSAGE_VALUE);
    }

    public void process(ChannelHandlerContext channel, Object object) {
        Msg message = (Msg) object;
        try {
            Msg.Message msg = message.getData().unpack(Msg.Message.class);
            System.out.println(msg.getName());
            MessageToAckMap.put(message.getSeq(), new MessageToAck(message, channel.channel(), message.getSeq()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
