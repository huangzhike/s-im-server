package mmp.im.gate.service;

import mmp.im.common.util.mq.MQConsumer;


public class MQHandler extends MQConsumer {


    public MQHandler(String mqURI, String consumeFromQueue) {
        super(mqURI, consumeFromQueue);
    }

    @Override
    public boolean process(byte[] contentBody) {
        try {
            System.out.println(contentBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
