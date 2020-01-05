package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyConstant;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.connector.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static mmp.im.common.protocol.ProtobufMessage.ClientLogout;
import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

public class ClientLogoutHandler  implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientLogout.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();


        ClientLogout message = (ClientLogout) object;

        LOG.warn("ClientLogout... {}", message);

        Map<Long, Long> receivedCache = channel.attr(AttributeKeyConstant.REV_SEQ_CACHE).get();

        if (receivedCache.containsKey(message.getSeq())) {
            LOG.warn("repeat");
            return;
        }

        // 加入已收到的消息
        receivedCache.putIfAbsent(message.getSeq(), message.getSeq());

        String userId = channel.attr(AttributeKeyConstant.CHANNEL_ID).get();
        // 不需回复确认

        // 移除并关闭
        AcceptorChannelManager.getInstance().removeChannel(userId);

        // 生成消息待转发
        ClientStatus m = MessageBuilder.buildClientStatus(message.getUserId(), Config.SERVER_ID , false, message.getClientInfo());

        // distribute
        MessageSender.sendToAcceptor(m);

        // 发的消息待确认
        ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);


    }
}

