package mmp.im.common.server.tcp.handler.channel;

import com.google.protobuf.Any;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import mmp.im.common.protocol.AcknowledgeBody;
import mmp.im.common.protocol.ClientMessageBody;
import mmp.im.common.server.tcp.cache.ack.ResendMessage;
import mmp.im.common.server.tcp.cache.ack.ResendMessageMap;
import mmp.im.common.server.tcp.util.SeqGenerator;
import mmp.im.common.util.mq.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(getClass());


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                LOG.warn("IdleState.WRITER_IDLE");

                ClientMessageBody.ClientMessage.Builder c = ClientMessageBody.ClientMessage.newBuilder();

                c.setSeq(SeqGenerator.get());
                c.setFrom("from mmp");
                c.setTo("to mmp");
                c.setType(ClientMessageBody.ClientMessage.MessageType.FRIEND);

                ClientMessageBody.ClientMessage.Friend.Builder f = ClientMessageBody.ClientMessage.Friend.newBuilder();
                f.setName("尼玛的 为什么");

                c.setData(Any.pack(f.build()));
                LOG.warn("WRITER_IDLE...  WRITER_IDLE --> {} : " + c.build());
                ctx.channel().writeAndFlush(c.build());



                ResendMessageMap.put(c.getSeq(), new ResendMessage(c.build(), ctx.channel(), c.getSeq()));


                // ctx.channel().writeAndFlush(HeartbeatBody.Heartbeat.newBuilder().build());
                // LOG.warn("IdleState.WRITER_IDLE HeartbeatBody -> {}", HeartbeatBody.Heartbeat.newBuilder().build());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
