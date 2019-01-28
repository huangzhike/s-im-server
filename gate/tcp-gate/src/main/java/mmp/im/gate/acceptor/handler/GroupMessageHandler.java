package mmp.im.gate.acceptor.handler;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.IMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.GroupMessage;


public class GroupMessageHandler   implements IMessageHandler {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String name = GroupMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }



    @Override
    public void process(ChannelHandlerContext channelHandlerContext, Object object) {

        GroupMessage message = (GroupMessage) object;

        // Message m = (Message) MessageBuilder.buildGroupMessage(message.getFrom(), message.getTo(), msg);
        // 转发到中心server
        // SpringContextHolder.getBean(MessageSender.class).sendToAcceptor(m);


    }
}

