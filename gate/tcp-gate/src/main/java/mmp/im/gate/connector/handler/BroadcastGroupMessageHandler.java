package mmp.im.gate.connector.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;

public class BroadcastGroupMessageHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = GroupMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }


    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        GroupMessage message = (GroupMessage) object;

        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        List<String> idList = message.getBroadcastIdListList();

        if (idList == null) {
            return;
        }
        // 收到distribute推送，好友广播
        for (String id : idList) {
            GroupMessage m = MessageBuilder.buildTransGroupMessage(message);
            ContextHolder.getMessageSender().sendToConnector(m, id);
        }

        ReferenceCountUtil.release(object);
    }
}

