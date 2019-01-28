package mmp.im.gate.acceptor;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.server.channel.handler.AcceptorIdleStateTrigger;
import mmp.im.common.server.codec.decode.WebsocketMessageDecoder;
import mmp.im.common.server.codec.encode.WebsocketMessageEncoder;
import mmp.im.common.server.event.EventHandler;
import mmp.im.common.server.server.AbstractTCPAcceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@Data
@Accessors(chain = true)
public class ClientToGateAcceptor extends AbstractTCPAcceptor {

    public ClientToGateAcceptor(Integer port) {
        this.port = port;
    }

    @Autowired
    private ClientToGateAcceptorHandler clientToGateAcceptorHandler;

    private String serveId;

    @Override
    public void bind() {
        this.serverBootstrap
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline pipeline = ch.pipeline();

                        // HTTP请求的解码和编码
                        pipeline.addLast(new HttpServerCodec());
                        // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
                        // 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        // 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
                        pipeline.addLast(new ChunkedWriteHandler());
                        // WebSocket数据压缩
                        pipeline.addLast(new WebSocketServerCompressionHandler());

                        pipeline.addFirst(new LoggingHandler(LogLevel.INFO)); // 打印日志,可以看到websocket帧数据
                        pipeline.addLast(new HttpServerCodec()); // 将请求和应答消息编码或解码为HTTP消息
                        pipeline.addLast(new HttpObjectAggregator(65536)); // 将HTTP消息的多个部分组合成一条完整的HTTP消息
                        pipeline.addLast(new WebSocketServerCompressionHandler());
                        pipeline.addLast(new WebSocketServerProtocolHandler("websocket地址/ws", null, true));

                        pipeline.addLast(new WebsocketMessageDecoder());
                        pipeline.addLast(new WebsocketMessageEncoder());

                        // 60s没有read事件，触发userEventTriggered事件，指定IdleState的类型为READER_IDLE
                        // client每隔30s发送一个心跳包，如果60s都没有收到心跳，说明链路发生了问题
                        pipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new AcceptorIdleStateTrigger());
                        pipeline.addLast(clientToGateAcceptorHandler);
                        pipeline.addLast(new EventHandler(null));

                    }
                });
        try {

            LOG.warn("binding port... {}", this.port);
            ChannelFuture future = this.serverBootstrap.bind(new InetSocketAddress(this.port)).sync();
            LOG.warn("closeFuture sync...");
            future.channel().closeFuture().sync();
            LOG.warn("future...");
        } catch (Exception e) {
            LOG.error("bind Exception... {}", e);
        } finally {
            this.bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            this.workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }

    }

}
