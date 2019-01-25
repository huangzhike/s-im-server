package mmp.im.gate.configuration;

import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendMessageMapConfig {

    @Bean
    public ResendMessageMap resendMessageMap() {
        return new ResendMessageMap();
    }

}
