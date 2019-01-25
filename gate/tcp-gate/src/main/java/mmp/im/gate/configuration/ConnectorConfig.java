package mmp.im.gate.configuration;


import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.tcp.cache.connection.ConnectorChannelHolder;
import mmp.im.gate.connector.GateToDistConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorConfig {


    @Bean
    public ConnectorChannelHolder connectorChannelHolder() {
        return new ConnectorChannelHolder();
    }


    @Value("${gateToDistAcceptor.connect.port}")
    private Integer gateToDistAcceptorPort;


    @Value("${gateToDistConnector.connect.host}")
    private String gateToDistConnectorHost;


    @Bean
    public GateToDistConnector gateToDistConnector() {
        GateToDistConnector gateToDistConnector = new GateToDistConnector(gateToDistConnectorHost, gateToDistAcceptorPort);

        return gateToDistConnector;
    }


    @Bean("gateToDistConnectorProtocolParserHolder")
    public ProtocolParserHolder protocolParserHolder() {
        return new ProtocolParserHolder("mmp.im.gate.protocol.parser", IProtocolParser.class);
    }


}
