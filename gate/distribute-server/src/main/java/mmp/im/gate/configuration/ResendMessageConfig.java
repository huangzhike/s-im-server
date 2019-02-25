package mmp.im.gate.configuration;


import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.acknowledge.ResendMessageThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendMessageConfig {

    @Bean
    public ResendMessageMap resendMessageMap() {
        return new ResendMessageMap();
    }

    @Bean
    public ResendMessageThread resendMessageThread(ResendMessageMap resendMessageMap) {
        return new ResendMessageThread(resendMessageMap);
    }

}
