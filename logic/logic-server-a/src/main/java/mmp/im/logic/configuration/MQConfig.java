package mmp.im.logic.configuration;


import mmp.im.common.protocol.parser.IMQProtocolParser;
import mmp.im.common.util.mq.MQConsumer;
import mmp.im.logic.protocol.parser.ProtocolParserHolder;
import mmp.im.common.protocol.ParserPacket;
import mmp.im.common.protocol.parser.IProtocolParser;
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

                // 获取包头中的类型
                byte protocolType = contentBody[0];
                ParserPacket parserPacket = new ParserPacket().setProtocolType(protocolType)
                        .setBody(Arrays.copyOfRange(contentBody, 1, contentBody.length));
                IMQProtocolParser protocolParser = ProtocolParserHolder.get(parserPacket.getProtocolType());

                if (protocolParser != null) {
                    protocolParser.parse(parserPacket.getBody());
                }

                return true;
            }
        };

    }
}
