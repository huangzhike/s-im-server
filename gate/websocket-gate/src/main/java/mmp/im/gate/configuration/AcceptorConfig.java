package mmp.im.gate.configuration;


import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.gate.acceptor.ClientToGateAcceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcceptorConfig {


    @Bean
    public AcceptorChannelHandlerMap acceptorChannelHandlerMap() {
        return new AcceptorChannelHandlerMap();
    }


    @Value("${clientToGateAcceptor.bind.port}")
    private Integer clientToGateAcceptorPort;


    @Value("${acceptor.serverId}")
    private String serverId;

    @Bean
    public ClientToGateAcceptor clientToGateAcceptor() {

        ClientToGateAcceptor clientToGateAcceptor = new ClientToGateAcceptor(clientToGateAcceptorPort);
        clientToGateAcceptor.setServeId(serverId);
        return clientToGateAcceptor;
    }


    @Bean("clientToGateAcceptorProtocolParserHolder")
    public ProtocolParserHolder protocolParserHolder() {
        return new ProtocolParserHolder("mmp.im.gate.acceptor.parser", IProtocolParser.class);
    }


}
