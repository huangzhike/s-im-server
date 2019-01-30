package mmp.im.gate.configuration;


import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.acknowledge.ResendMessageThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendMessageThreadConfig {


    @Autowired
    private ResendMessageMap resendMessageMap;

    @Bean
    public ResendMessageThread resendMessageThread() {
        return new ResendMessageThread(resendMessageMap);
    }

}
