package mmp.im.gate.configuration;


import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.protocol.handler.NettyMessageHandlerHolder;
import mmp.im.common.server.cache.connection.AcceptorChannelMap;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import mmp.im.gate.acceptor.GateToDistAcceptorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcceptorConfig {

    //////////////

    @Value("${gateToDistAcceptor.bind.port}")
    private Integer gateToDistAcceptorPort;


    //////////////
    @Value("${acceptor.serverId}")
    private Long serverId;
    @Bean
    public AcceptorChannelMap acceptorChannelHandlerMap() {
        return new AcceptorChannelMap();
    }
    @Bean
    public GateToDistAcceptorHandler gateToDistAcceptorHandler(AcceptorChannelMap acceptorChannelMap) {
        GateToDistAcceptorHandler acceptorHandler = new GateToDistAcceptorHandler();
        acceptorHandler.setNettyMessageHandlerHolder(new NettyMessageHandlerHolder("mmp.im.gate.acceptor.handler", INettyMessageHandler.class));
        acceptorHandler.setAcceptorChannelMap(acceptorChannelMap);
        return acceptorHandler;
    }


    @Bean
    public GateToDistAcceptor clientToGateAcceptor(GateToDistAcceptorHandler gateToDistAcceptorHandler) {
        GateToDistAcceptor clientToGateAcceptor = new GateToDistAcceptor(gateToDistAcceptorPort);
        clientToGateAcceptor.setAcceptorHandler(gateToDistAcceptorHandler);

        clientToGateAcceptor.setServeId(serverId);
        return clientToGateAcceptor;
    }

}
