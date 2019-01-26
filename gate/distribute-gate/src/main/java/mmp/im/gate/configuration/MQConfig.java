package mmp.im.gate.configuration;


import com.google.protobuf.MessageLite;
import com.rabbitmq.client.MessageProperties;
import mmp.im.common.protocol.util.ProtocolUtil;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.common.util.mq.ResendElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;

@Configuration
public class MQConfig {


    @Value("${mq.produceToQueue}")
    private String produceToQueue;

    @Value("${mq.mqURI}")
    private String mqURI;

    @Bean
    public MQProducer mqProducer() {

        return new MQProducer(mqURI, produceToQueue) {
            @Override
            public boolean publish(String exchangeName, String routingKey, Object msg) {
                boolean ok = false;

                MessageLite messageLite = (MessageLite) msg;
                LOG.warn("publish msg... {}", msg);
                try {
                    byte[] contents = encode(messageLite);

                    this.pubChannel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, contents);

                    LOG.warn("publish byte... {}", contents);
                    ok = true;
                } catch (Exception e) {
                    LOG.error("publish Exception... {}", e);
                    resendQueue.add(new ResendElement(exchangeName, routingKey, msg));
                }
                return ok;
            }

            @Override
            public boolean pub(Object msg) {
                return this.publish("", "", msg);
            }

            private byte[] encode(MessageLite msg) {
                byte protocolType = ProtocolUtil.encodeProtocolType(msg);
                byte[] body = msg.toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(body.length + 1); // 声明一个缓冲区
                buffer.put(protocolType);
                buffer.put(body);
                return buffer.array();
            }
        };

    }
}
