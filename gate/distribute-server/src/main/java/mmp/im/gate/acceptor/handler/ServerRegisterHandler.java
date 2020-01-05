package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.model.GateInfo;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static mmp.im.common.protocol.ProtobufMessage.ServerRegister;

public class ServerRegisterHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ServerRegister.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();

        String sId = channel.attr(AttributeKeyConstant.CHANNEL_ID).get();
        if (sId != null) {
            LOG.warn("already login");
            return;
        }


        ServerRegister message = (ServerRegister) object;

        LOG.warn("ServerRegister... {}", message);

        String serverId = message.getSeverId();

        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        channel.attr(AttributeKeyConstant.CHANNEL_ID).set(serverId);
        // 说明是重复发送，不处理，只回复ACK
        Map<Long, Long> receivedCache = channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).get();

        if (receivedCache.containsKey(message.getSeq())) {
            LOG.warn("repeat");
            return;
        }

        // 加入已收到的消息
        receivedCache.putIfAbsent(message.getSeq(), message.getSeq());


        // 添加进channel map
        AcceptorChannelManager.getInstance().addChannel(serverId, channelHandlerContext);

        // 通道Id
        channelHandlerContext.channel().attr(AttributeKeyConstant.CHANNEL_ID).set(serverId);

        GateInfo gateInfo = new GateInfo();
        gateInfo.setIp(channel.remoteAddress().toString());
        gateInfo.setServerId(serverId);
        // Gate状态
        GateInfo.TYPE t = message.getType().getNumber() == ServerRegister.Type.WEBSOCKET_VALUE ? GateInfo.TYPE.WEBSOCKET : GateInfo.TYPE.TCP;
        gateInfo.setType(t);
        ContextHolder.getServerService().updateGateList(gateInfo);


    }
}


