package mmp.im.logic.util;

import mmp.im.common.util.mq.MQConsumer;

public class MQProcessor extends MQConsumer {

    public MQProcessor(String mqURI, String consumeFromQueue) {
        super(mqURI, consumeFromQueue);
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
