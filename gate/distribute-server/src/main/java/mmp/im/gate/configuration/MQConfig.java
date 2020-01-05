package mmp.im.gate.configuration;


import com.google.protobuf.MessageLite;
import com.rabbitmq.client.MessageProperties;
import mmp.im.common.protocol.util.ProtocolUtil;
import mmp.im.common.util.mq.MQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;

@Configuration
public class MQConfig {


    @Value("${mq.produceToQueue}")
    private String produceToQueue;

    @Value("${mq.exchange}")
    private String exchange;

    @Value("${mq.routingKey}")
    private String routingKey;


    @Value("${mq.mqURI}")
    private String mqURI;

    @Bean
    public MQProducer mqProducer() {

        return new MQProducer(mqURI, exchange, routingKey, produceToQueue) {


            @Override
            public boolean publish(Object msg) {

                byte[] contents = encode((MessageLite) msg);

                boolean ok = false;

                try {
                    this.pubChannel.basicPublish(this.exchange, this.routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, contents);
                    ok = true;
                } catch (Exception e) {

                    LOG.error(e.getLocalizedMessage());

                    this.addToResend(msg);
                }

                return ok;

            }

            private byte[] encode(MessageLite msg) {
                byte protocolType = ProtocolUtil.encodeCommandId(msg);
                byte[] body = msg.toByteArray();
                // 声明一个堆缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(body.length + 1);
                buffer.put(protocolType);
                buffer.put(body);
                buffer.flip();
                return buffer.array();
            }
        };

    }
}
