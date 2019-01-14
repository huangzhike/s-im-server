package mmp.im.logic.util;

import com.google.protobuf.MessageLite;
import com.rabbitmq.client.MessageProperties;
import mmp.im.common.util.mq.MQ;

public class MQProcessor extends MQ {

    public MQProcessor(String mqURI, String publishToQueue, String consumeFromQueue) {
        super(mqURI, publishToQueue, consumeFromQueue);
    }

    @Override
    public boolean publish(String exchangeName, String routingKey, Object msg) {
        return true;
    }

    @Override
    protected boolean process(byte[] contentBody) {
        try {
            System.out.println(contentBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private Object decode(byte[] bytes) {
        return null;
    }
}
