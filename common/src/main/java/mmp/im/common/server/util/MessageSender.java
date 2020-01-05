package mmp.im.common.server.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.connection.AcceptorChannelManager;
import mmp.im.common.server.connection.ConnectorChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
@Accessors(chain = true)
public class MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    private static AcceptorChannelManager acceptorChannelMap = AcceptorChannelManager.getInstance();

    private static ConnectorChannelHolder connectorChannelHolder = ConnectorChannelHolder.getInstance();


    public static void reply(ChannelHandlerContext channelHandlerContext, MessageLite messageLite) {
        channelHandlerContext.channel().writeAndFlush(messageLite);
    }

    public static void sendToConnector(MessageLite messageLite, String key) {

        if (acceptorChannelMap != null) {
            ChannelHandlerContext channelHandlerContext = acceptorChannelMap.getChannel(key);
            sendTo(messageLite, channelHandlerContext);
        }
    }


    public static void sendToAcceptor(MessageLite messageLite) {

        if (connectorChannelHolder != null) {
            ChannelHandlerContext channelHandlerContext = connectorChannelHolder.getChannelHandlerContext();
            sendTo(messageLite, channelHandlerContext);
        }

    }

    private static void sendTo(MessageLite messageLite, ChannelHandlerContext channelHandlerContext) {

        if (channelHandlerContext == null) {
            return;
        }

        // ChannelHandlerContext.writeAndFlush()只是产生write任务和flush任务，交由Netty的IO线程处理，
        // 其ChannelFuture返回时并不保证flush任务已执行完毕并将数据写入到了连接对应的TCP发送缓冲区。
        // 如果不对writeAndFlush()的调用速率进行限制，当应用对socket进行写操作的速率超过socket实际发送速率时，
        // 对应连接的TCP发送缓冲区会被填满，后续写操作的待发送数据会由Netty不断申请内存缓存，最终导致内存溢出。

        // 若状态为可写入说明待发送数据量并未堆积超限，可以进行异步发送
        if (channelHandlerContext.channel().isWritable()) {
            LOG.warn("sendTo... {}", messageLite);
            channelHandlerContext.channel().writeAndFlush(messageLite).addListener(future -> {
                if (!future.isSuccess()) {
                }
            });
        }
        // 否则需要执行sync()方法等待发送数据真正成功写入socket的TCP发送缓冲区，然后在执行下一次的write操作，防止write速率过快导致内存溢出。
        else {
            try {
                channelHandlerContext.channel().writeAndFlush(messageLite).sync();

            } catch (Exception e) {
                LOG.error(e.getLocalizedMessage());
            }
        }


    }


}
