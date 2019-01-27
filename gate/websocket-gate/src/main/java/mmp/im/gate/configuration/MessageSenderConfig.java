package mmp.im.gate.configuration;

import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import mmp.im.common.server.util.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessageSenderConfig {


    @Autowired
    private ResendMessageMap resendMessageMap;


    @Autowired
    private ConnectorChannelHolder connectorChannelHolder;

    @Autowired
    private AcceptorChannelHandlerMap acceptorChannelHandlerMap;

    @Bean
    public MessageSender messageSender() {

        MessageSender messageSender = new MessageSender();
        messageSender.setResendMessageMap(resendMessageMap);
        messageSender.setConnectorChannelHolder(connectorChannelHolder);
        messageSender.setAcceptorChannelHandlerMap(acceptorChannelHandlerMap);
        return messageSender;
    }


}
