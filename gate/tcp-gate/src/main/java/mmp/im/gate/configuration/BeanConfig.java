package mmp.im.gate.configuration;


import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {


    @Bean
    public ProtocolParserHolder protocolParserHolder() {
        return new ProtocolParserHolder();
    }


    @Bean
    public MessageHandlerHolder messageHandlerHolder() {
        return new MessageHandlerHolder("", IMessageHandler.class);
    }

}
