package mmp.im.gate.protocol.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.gate.protocol.handler.IMsgTypeHandler;
import mmp.im.gate.util.PackageUtil;
import mmp.im.protocol.MessageBody;
import mmp.im.protocol.ProtocolHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCPProtocolParser implements IProtocolParser {

    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classes = PackageUtil.getSubClasses("mmp.im.gate.protocol", IMsgTypeHandler.class);

        classes.forEach(v -> {
            try {
                IMsgTypeHandler e = (IMsgTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getName(), e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }


    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.MESSAGE.getType();
    }

    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        MessageBody.Msg msg = null;
        try {
            msg = MessageBody.Msg.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (msg != null) {
            String type = String.valueOf(msg.getType().getNumber());
            IMsgTypeHandler handler = (IMsgTypeHandler) this.getMsgTypeHandlers().get(type);
            if (handler != null) {
                handler.process(channelHandlerContext, msg);
            }
        }


    }

    public Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}
