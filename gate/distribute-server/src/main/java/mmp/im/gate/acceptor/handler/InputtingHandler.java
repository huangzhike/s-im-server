package mmp.im.gate.acceptor.handler;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import mmp.im.common.model.Info;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.server.util.MessageBuilder;
import mmp.im.common.util.token.JWTUtil;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static mmp.im.common.protocol.ProtobufMessage.Inputting;

public class InputtingHandler extends CheckHandler implements INettyMessageHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final String name = Inputting.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();
        if (!this.login(channel)) {
            LOG.warn("未登录");
        }

        Inputting message = (Inputting) object;

        LOG.warn("Inputting... {}", message);

        // 查找好友登陆server列表 推送到server
        Set<String> friendServerList = ContextHolder.getStatusService().getUserServerList(message.getTo());
        LOG.warn("friendServerList... {}", friendServerList);


        if (friendServerList != null) {
            friendServerList.forEach(serverid -> {
                Inputting m = MessageBuilder.buildTransInputting(message);
                ContextHolder.getMessageSender().sendToConnector(m, serverid);
                LOG.warn("m... {}", m);
            });

        }

        ReferenceCountUtil.release(object);
    }
}


