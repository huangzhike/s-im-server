package mmp.im.gate.util;

import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.connection.AcceptorChannelMap;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import mmp.im.gate.service.StatusService;
import mmp.im.gate.service.XService;

public class ContextHolder {

    public static ResendMessageMap getResendMessageMap() {
        return SpringContextHolder.getBean(ResendMessageMap.class);
    }
    public static AcceptorChannelMap getAcceptorChannelMap() {
        return SpringContextHolder.getBean(AcceptorChannelMap.class);
    }
    public static MessageSender getMessageSender() {
        return SpringContextHolder.getBean(MessageSender.class);
    }

    public static String getServeId() {
        return SpringContextHolder.getBean(GateToDistAcceptor.class).getServeId();
    }

    public static MQProducer getMQProducer() {
        return SpringContextHolder.getBean(MQProducer.class);
    }

    public static StatusService getStatusService(){
        return SpringContextHolder.getBean(StatusService.class);
    }

    public static XService getXService(){
        return SpringContextHolder.getBean(XService.class);
    }



}
