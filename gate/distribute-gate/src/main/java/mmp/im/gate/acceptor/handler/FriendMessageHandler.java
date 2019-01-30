package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import mmp.im.gate.util.SeqGenerator;

import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.FriendMessage;

public class FriendMessageHandler implements INettyMessageHandler {


    private final String name = FriendMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        FriendMessage message = (FriendMessage) object;
        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().pub(message);

        // 单聊消息

        // 查找好友登陆server列表 推送到server
        // TODO 自己的别的端也要同步
        List<Info> friendServerList = ContextHolder.getStatusService().getUserServerList(message.getTo());

        if (friendServerList != null) {
            for (Info info : friendServerList) {
                // 生成序列号
                FriendMessage m = MessageBuilder.buildTransFriendMessage(message, SeqGenerator.get());
                ContextHolder.getMessageSender().sendToConnector(m, info.getServerInfo());
                // 发的消息待确认
                ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));

            }
        }

        ReferenceCountUtil.release(object);
    }
}

