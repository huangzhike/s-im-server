package mmp.im.client.configuration;

import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendMessageMapConfig {

    @Bean
    public ResendMessageMap resendMessageMap() {
        return new ResendMessageMap();
    }

}
