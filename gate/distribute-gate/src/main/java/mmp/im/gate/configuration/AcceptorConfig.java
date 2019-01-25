package mmp.im.gate.configuration;


import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.tcp.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcceptorConfig {


    @Bean
    public AcceptorChannelHandlerMap acceptorChannelHandlerMap() {
        return new AcceptorChannelHandlerMap();
    }


    @Value("${gateToDistAcceptor.bind.port}")
    private Integer gateToDistAcceptorPort;


    @Bean
    public GateToDistAcceptor gateToDistAcceptor() {
        return new GateToDistAcceptor(gateToDistAcceptorPort);
    }

    @Bean
    public ProtocolParserHolder protocolParserHolder() {
        return new ProtocolParserHolder("mmp.im.gate.protocol.parser", IProtocolParser.class);
    }


}
