package mmp.im.logic.service;

import mmp.im.common.util.mq.MQ;


public class MQHandler extends MQ {


    public MQHandler(String mqURI, String publishToQueue, String consumeFromQueue) {
        super(mqURI, publishToQueue, consumeFromQueue);
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
