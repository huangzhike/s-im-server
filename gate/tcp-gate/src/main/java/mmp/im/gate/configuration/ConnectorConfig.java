package mmp.im.gate.configuration;


import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;
import mmp.im.common.protocol.handler.INettyMessageHandler;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import mmp.im.common.server.channel.handler.ReconnectHandler;
import mmp.im.common.server.channel.initializer.ConnectorChannelInitializer;
import mmp.im.common.server.codec.decode.MessageDecoder;
import mmp.im.common.server.codec.encode.MessageEncoder;
import mmp.im.common.server.util.MessageSender;
import mmp.im.gate.connector.GateToDistConnector;
import mmp.im.gate.connector.GateToDistConnectorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ConnectorConfig {


    //////////////
    @Bean
    public ConnectorChannelHolder connectorChannelHolder() {
        return new ConnectorChannelHolder();
    }

    @Autowired
    private ConnectorChannelHolder connectorChannelHolder;


    //////////////

    @Value("${gateToDistAcceptor.connect.port}")
    private Integer gateToDistAcceptorPort;


    @Value("${gateToDistConnector.connect.host}")
    private String gateToDistConnectorHost;



    @Bean
    public GateToDistConnector gateToDistConnector() {
        GateToDistConnector gateToDistConnector = new GateToDistConnector(gateToDistConnectorHost, gateToDistAcceptorPort);

        ConnectorChannelInitializer channelInitializer = new ConnectorChannelInitializer();
        GateToDistConnectorHandler connectorHandler = new GateToDistConnectorHandler();
        connectorHandler.setMessageHandlerHolder(new MessageHandlerHolder("mmp.im.gate.connector.handler", INettyMessageHandler.class));
        connectorHandler.setConnectorChannelHolder(connectorChannelHolder);


        ChannelHandler[] handlerHolder = new ChannelHandler[]{
                new MessageDecoder(),
                new MessageEncoder(),
                // 每隔30s触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                // 实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端
                connectorHandler,
                new ReconnectHandler(gateToDistConnector)
        };

        channelInitializer.setChannelHandler(handlerHolder);

        gateToDistConnector.setChannelInitializer(channelInitializer);

        return gateToDistConnector;
    }


}
