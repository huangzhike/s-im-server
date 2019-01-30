package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.cache.acknowledge.ResendMessage;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.gate.util.ContextHolder;

import java.util.ArrayList;
import java.util.List;

import static mmp.im.common.protocol.ProtobufMessage.ClientStatus;

public class ClientStatusHandler implements INettyMessageHandler {


    private final String name = ClientStatus.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {


        ClientStatus message = (ClientStatus) object;

        // 回复确认收到消息
        ContextHolder.getMessageSender().reply(channelHandlerContext, MessageBuilder.buildAcknowledge(message.getSeq()));

        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().pub(message);

        Info info = new Info();
        // 在哪个Gate
        info.setServerInfo(message.getServerId());

        if (message.getStatus()) {
            // 登录
            ContextHolder.getStatusService().putUserServerList(message.getUserId(), info);
        } else {
            // 登出
            ContextHolder.getStatusService().removeUserServerList(message.getUserId(), info);
        }

        // 获取连接的Gate
        List<String> serverList = ContextHolder.getAcceptorChannelMap().getChannelMapKeyList();

        // 查找好友列表
        List<String> userFriendList = ContextHolder.getXService().getUserFriendIdList(message.getUserId());

        // TODO
        // 自己别的端登录情况
        List<Info> userServerList = ContextHolder.getStatusService().getUserServerList(message.getUserId());


        if (serverList != null) {
            for (String serverId : serverList) {
                // 某个Gate需要下发的用户列表
                List<String> serverUserList = new ArrayList<>();
                if (userFriendList != null) {
                    for (String userId : userFriendList) {
                        // 获取好友状态
                        List<Info> friendServerList = ContextHolder.getStatusService().getUserServerList(userId);
                        if (friendServerList != null) {
                            for (Info i : friendServerList) {
                                if (serverId.equals(i.getServerInfo())) {
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
                ContextHolder.getMessageSender().sendToConnector(m, serverId);
                // 发的消息待确认
                ContextHolder.getResendMessageMap().put(m.getSeq(), new ResendMessage(m.getSeq(), m, channelHandlerContext));


            }

        }


        ReferenceCountUtil.release(object);

    }
}

