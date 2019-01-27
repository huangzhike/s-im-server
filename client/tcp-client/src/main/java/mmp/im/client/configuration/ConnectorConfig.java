package mmp.im.client.configuration;


import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import mmp.im.client.connector.ClientToGateConnector;
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
    public ClientToGateConnector gateToDistConnector() {
        ClientToGateConnector clientToGateConnector = new ClientToGateConnector(gateToDistConnectorHost, gateToDistAcceptorPort);

        return clientToGateConnector;
    }


    @Bean("gateToDistConnectorProtocolParserHolder")
    public ProtocolParserHolder protocolParserHolder() {
        return new ProtocolParserHolder("mmp.im.client.connector.parser", IProtocolParser.class);
    }


}
