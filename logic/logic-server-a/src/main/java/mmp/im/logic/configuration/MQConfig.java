package mmp.im.logic.configuration;


import mmp.im.common.protocol.parser.IMQProtocolParser;
import mmp.im.common.util.mq.MQConsumer;
import mmp.im.logic.protocol.ProtocolParserHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class MQConfig {


    @Value("${mq.consumeFromQueue}")
    private String consumeFromQueue;

    @Value("${mq.mqURI}")
    private String mqURI;

    @Bean
    public MQConsumer mqConsumer() {

        return new MQConsumer(mqURI, consumeFromQueue) {

            @Override
            public boolean process(byte[] contentBody) {

                LOG.warn("contentBody... {}", contentBody);
                // 获取包头中的类型
                byte protocolType = contentBody[0];

                LOG.warn("protocolType... {}", protocolType);

                IMQProtocolParser protocolParser = ProtocolParserHolder.get(protocolType);

                if (protocolParser != null) {

                    LOG.warn("protocolParser... {}", protocolParser);

                    protocolParser.parse(Arrays.copyOfRange(contentBody, 1, contentBody.length));
                }

                return true;
            }
        };

    }
}
