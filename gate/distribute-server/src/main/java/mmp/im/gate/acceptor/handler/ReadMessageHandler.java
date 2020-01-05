package mmp.im.gate.acceptor.handler;


import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.gate.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mmp.im.common.protocol.ProtobufMessage.ReadMessage;

public class ReadMessageHandler   implements INettyMessageHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final String name = ReadMessage.getDefaultInstance().getClass().toString();

    @Override
    public String getHandlerName() {
        return this.name;
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, MessageLite object) {

        Channel channel = channelHandlerContext.channel();


        ReadMessage message = (ReadMessage) object;

        // 推送到logic处理，数据库操作等
        ContextHolder.getMQProducer().publish(message);


    }
}



