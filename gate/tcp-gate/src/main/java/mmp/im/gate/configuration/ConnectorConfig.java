package mmp.im.gate.configuration;


import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import mmp.im.gate.connector.GateToDistConnector;
import mmp.im.gate.connector.GateToDistConnectorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorConfig {


    //////////////
    @Bean
    public ConnectorChannelHolder connectorChannelHolder() {
        return new ConnectorChannelHolder();
    }

    //////////////

    @Value("${gateToDistAcceptor.connect.port}")
    private Integer gateToDistAcceptorPort;


    @Value("${gateToDistConnector.connect.host}")
    private String gateToDistConnectorHost;


    @Bean
    public GateToDistConnectorHandler gateToDistConnectorHandler(ConnectorChannelHolder connectorChannelHolder) {
        GateToDistConnectorHandler connectorHandler = new GateToDistConnectorHandler();
        connectorHandler.setMessageHandlerHolder(new MessageHandlerHolder("mmp.im.gate.connector.handler", INettyMessageHandler.class));
        connectorHandler.setConnectorChannelHolder(connectorChannelHolder);

        return connectorHandler;
    }

    @Bean
    public GateToDistConnector gateToDistConnector(GateToDistConnectorHandler connectorHandler ) {
        GateToDistConnector gateToDistConnector = new GateToDistConnector(gateToDistConnectorHost, gateToDistAcceptorPort);
        gateToDistConnector.setGateToDistConnectorHandler(connectorHandler);

        return gateToDistConnector;
    }


}
