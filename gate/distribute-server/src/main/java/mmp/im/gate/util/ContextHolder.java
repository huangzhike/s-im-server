package mmp.im.gate.util;

import im.database.service.ServerService;
import im.database.service.StatusService;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.gate.database.service.XService;

public class ContextHolder {



    public static MQProducer getMQProducer() {
        return SpringContextHolder.getBean(MQProducer.class);
    }

    public static StatusService getStatusService() {
        return SpringContextHolder.getBean(StatusService.class);
    }

    public static XService getXService() {
        return SpringContextHolder.getBean(XService.class);
    }

    public static ServerService getServerService() {
        return SpringContextHolder.getBean(ServerService.class);
    }


}
