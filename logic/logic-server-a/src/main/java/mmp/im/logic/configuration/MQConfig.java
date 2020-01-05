package mmp.im.logic.configuration;


import com.google.protobuf.MessageLite;
import mmp.im.common.protocol.handler.IMessageHandler;
import mmp.im.common.protocol.handler.MessageHandlerHolder;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.util.mq.MQConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MQConfig {


    @Value("${mq.consumeFromQueue}")
    private String consumeFromQueue;

    @Value("${mq.exchange}")
    private String exchange;

    @Value("${mq.routingKey}")
    private String routingKey;

    @Value("${mq.mqURI}")
    private String mqURI;


    @Bean
    public MQConsumer mqConsumer() {


        ProtocolParserHolder protocolParserHolder = new ProtocolParserHolder();

        MessageHandlerHolder messageHandlerHolder = new MessageHandlerHolder("mmp.im.logic.handler", IMessageHandler.class);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);


        return new MQConsumer(mqURI, consumeFromQueue) {

            @Override
            public boolean process(byte[] contentBody) {

                LOG.warn("contentBody... {}", contentBody);
                // 获取包头中的类型
                byte commandId = contentBody[0];

                LOG.warn("commandId... {}", commandId);

                IProtocolParser protocolParser = protocolParserHolder.get(commandId);
                if (protocolParser != null) {
                    MessageLite msg = (MessageLite) protocolParser.parse(Arrays.copyOfRange(contentBody, 1, contentBody.length));
                    LOG.warn("finished {}", msg);
                    if (msg != null) {
                        executorService.submit(() -> {
                            messageHandlerHolder.assignHandler(msg);
                        });
                    }

                }
                return true;
            }
        };

    }
}
