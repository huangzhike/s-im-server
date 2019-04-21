package mmp.im.gate.connector.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.message.ResendMessageManager;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

public class BroadcastClientStatusHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientStatus.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();

        ClientStatus message = (ClientStatus) object;

        LOG.warn("ClientStatus... {}", message);

        // 回复确认收到消息
        MessageSender.reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");

            return;
        }
        // 加入已收到的消息
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());

        // Gate需要转发的对象
        List<String> idList = message.getBroadcastIdListList();

        if (idList == null) {

            return;
        }
        // 收到distribute推送，好友广播
        for (String id : idList) {
            ClientStatus m = MessageBuilder.buildTransClientStatus(message);
            // 下发
            MessageSender.sendToConnector(m, id);
            // 发的消息待确认
            ResendMessageManager.getInstance().put(m.getSeq(), m, channelHandlerContext);

            LOG.warn("m... {}", m);
        }


    }
}

