package mmp.im.gate.configuration;

import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.tcp.cache.connection.AcceptorChannelHandlerMap;
import mmp.im.common.server.tcp.util.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessageSenderConfig {


    @Autowired
    private ResendMessageMap resendMessageMap;


    @Autowired
    private AcceptorChannelHandlerMap acceptorChannelHandlerMap;

    @Bean
    public MessageSender messageSender() {

        MessageSender messageSender = new MessageSender();
        messageSender.setResendMessageMap(resendMessageMap);
        messageSender.setAcceptorChannelHandlerMap(acceptorChannelHandlerMap);
        return messageSender;
    }


}
