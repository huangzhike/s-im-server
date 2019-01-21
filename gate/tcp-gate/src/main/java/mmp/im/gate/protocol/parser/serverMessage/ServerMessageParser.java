package mmp.im.gate.protocol.parser.serverMessage;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.AcknowledgeBody;
import mmp.im.common.protocol.ProtocolHeader;
import mmp.im.common.protocol.ServerMessageBody;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.server.tcp.util.AttributeKeyHolder;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.common.util.reflect.PackageUtil;
import mmp.im.gate.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMessageParser implements IProtocolParser {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private Map<String, Object> msgTypeHandlers;

    {
        this.msgTypeHandlers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol.handler.serverMessage", IMessageTypeHandler.class);

        classList.forEach(v -> {
            try {
                IMessageTypeHandler e = (IMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }


    @Override
    public int getProtocolKind() {
        return ProtocolHeader.ProtocolType.SERVER.getType();
    }

    @Override
    public void parse(ChannelHandlerContext channelHandlerContext, byte[] bytes) {

        String userId = channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).get();


        ServerMessageBody.ServerMessage msg = null;

        try {
            msg = ServerMessageBody.ServerMessage.parseFrom(bytes);

            // 没登陆就关闭
            if (userId == null && msg.getType() != ServerMessageBody.ServerMessage.MsgType.SERVER_LOGIN) {
                channelHandlerContext.channel().close();
                return;
            }

            if (!userId.equals(msg.getFrom())) {
                channelHandlerContext.channel().close();
                LOG.warn("非法消息，通道关闭");
                return;
            }

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (msg != null) {
            String type = String.valueOf(msg.getType().getNumber());
            IMessageTypeHandler handler = (IMessageTypeHandler) this.getMsgTypeHandlers().get(type);
            if (handler != null) {
                handler.process(channelHandlerContext, msg);
            }
            // 推送到logic处理，数据库操作等
            SpringContextHolder.getBean(MQProducer.class).publish("", "", msg);

            // 回复确认
            AcknowledgeBody.Acknowledge.Builder builder = AcknowledgeBody.Acknowledge.newBuilder();

            channelHandlerContext.channel().writeAndFlush(builder.setAck(msg.getSeq()).build());

        }


    }

    private Map<String, Object> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }


}
