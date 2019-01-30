package mmp.im.gate.configuration;


import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.server.cache.connection.AcceptorChannelMap;
import mmp.im.common.server.channel.initializer.AcceptorChannelInitializer;
import mmp.im.common.server.codec.decode.MessageDecoder;
import mmp.im.common.server.codec.encode.MessageEncoder;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import mmp.im.gate.acceptor.GateToDistAcceptorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AcceptorConfig {

    //////////////

    @Bean
    public AcceptorChannelMap acceptorChannelHandlerMap() {
        return new AcceptorChannelMap();
    }

    @Autowired
    private AcceptorChannelMap acceptorChannelMap;



    //////////////

    @Value("${gateToDistAcceptor.bind.port}")
    private Integer gateToDistAcceptorPort;

    @Value("${acceptor.serverId}")
    private String serverId;




    @Bean
    public GateToDistAcceptor clientToGateAcceptor() {
        GateToDistAcceptor clientToGateAcceptor = new GateToDistAcceptor(gateToDistAcceptorPort);

        AcceptorChannelInitializer acceptorChannelInitializer = new AcceptorChannelInitializer();
        GateToDistAcceptorHandler acceptorHandler = new GateToDistAcceptorHandler();
        acceptorHandler.setMessageHandlerHolder(new MessageHandlerHolder("mmp.im.gate.acceptor.handler", INettyMessageHandler.class));
        acceptorHandler.setAcceptorChannelMap(acceptorChannelMap);
        acceptorChannelInitializer.setChannelHandler(new ChannelHandler[]{
                new MessageDecoder(),
                new MessageEncoder(),
                // 60s没有read事件，触发userEventTriggered事件，指定IdleState的类型为READER_IDLE
                // client每隔30s发送一个心跳包，如果60s都没有收到心跳，说明链路发生了问题
                new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS),
                acceptorHandler,

        });

        clientToGateAcceptor.setChannelInitializer(acceptorChannelInitializer);
        clientToGateAcceptor.setServeId(serverId);
        return clientToGateAcceptor;
    }

}
