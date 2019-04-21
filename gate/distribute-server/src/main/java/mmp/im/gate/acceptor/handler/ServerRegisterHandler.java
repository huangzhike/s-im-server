package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.model.GateInfo;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static mmp.im.common.protocol.ProtobufMessage.ServerRegister;

public class ServerRegisterHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ServerRegister.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();
        // 已登录
        if (this.login(channel)) {

            LOG.warn("已登录");
            return;
        }

        ServerRegister message = (ServerRegister) object;

        LOG.warn("ServerRegister... {}", message);

        String serverId = message.getSeverId();

        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        channel.attr(AttributeKeyHolder.CHANNEL_ID).set(serverId);
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).set(new ArrayList<>());


        // 说明是重复发送，不处理，只回复ACK
        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");

            return;
        }

        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());


        // 添加进channel map
        AcceptorChannelManager.getInstance().addChannel(serverId, channelHandlerContext);

        // 通道Id
        channelHandlerContext.channel().attr(AttributeKeyHolder.CHANNEL_ID).set(serverId);

        GateInfo gateInfo = new GateInfo();
        gateInfo.setIp(channel.remoteAddress().toString());
        gateInfo.setServerId(serverId);
        // Gate状态
        GateInfo.TYPE t = message.getType().getNumber() == ServerRegister.Type.WEBSOCKET_VALUE ? GateInfo.TYPE.WEBSOCKET : GateInfo.TYPE.TCP;
        gateInfo.setType(t);
        ContextHolder.getServerService().updateGateList(gateInfo);



    }
}


