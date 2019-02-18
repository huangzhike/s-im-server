package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.AttributeKeyHolder;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

public class ClientStatusHandler extends CheckHandler implements INettyMessageHandler {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = ClientStatus.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        ClientStatus message = (ClientStatus) object;

        Channel channel = channelHandlerContext.channel();
        if (!this.login(channel)) {
            LOG.warn("未登录");
        }

        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        // 说明是重复发送，不处理，只回复ACK
        if (this.duplicate(channel, message.getSeq())) {
            LOG.warn("重复消息");
            // release
            return;
        }
        channel.attr(AttributeKeyHolder.REV_SEQ_LIST).get().add(message.getSeq());


        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().pub(message);

        Info info = new Info();
        // 在哪个Gate
        info.setServerId(message.getServerId());

        // 修改本地状态
        if (message.getStatus()) {
            // 登录
            ContextHolder.getStatusService().putUserServerList(message.getUserId(), info);
        } else {
            // 登出
            ContextHolder.getStatusService().removeUserServerList(message.getUserId(), info);
        }

        LOG.warn("ClientStatus... {}", message);

        // 获取所有连接的Gate id列表
        List<Long> serverList = ContextHolder.getAcceptorChannelMap().getChannelMapKeyList();

        LOG.warn("serverList... {}", serverList);


        // 查找好友id列表
        List<Long> userFriendList = ContextHolder.getXService().getUserFriendIdList(message.getUserId());
        LOG.warn("userFriendList... {}", userFriendList);

        // 自己别的端登录
        userFriendList.add(message.getUserId());


        if (serverList != null) {
            for (Long gateId : serverList) {
                // 某个Gate需要下发的用户列表
                List<Long> serverUserList = new ArrayList<>();
                if (userFriendList != null) {
                    for (Long userId : userFriendList) {
                        // 获取好友状态 登录在哪个Gate
                        List<Info> friendServerList = ContextHolder.getStatusService().getUserServerList(userId);
                        if (friendServerList != null) {
                            for (Info i : friendServerList) {
                                if (gateId.equals(i.getServerId())) {
                                    // 推送的Gate上添加该好友
                                    serverUserList.add(userId);
                                }
                            }

                        }
                    }
                }
                // 给该Gate下发的消息
                ClientStatus m = MessageBuilder.buildTransClientStatus(message, serverUserList);

                // distribute
                ContextHolder.getMessageSender().sendToConnector(m, gateId);
                // 发的消息待确认
                ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));
                LOG.warn("m... {}", m);
            }

        }


        ReferenceCountUtil.release(object);

    }
}

