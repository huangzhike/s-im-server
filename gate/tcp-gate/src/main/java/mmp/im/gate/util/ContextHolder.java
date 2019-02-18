package mmp.im.gate.util;

import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.connection.AcceptorChannelMap;
import mmp.im.common.server.util.MessageSender;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.gate.acceptor.ClientToGateAcceptor;

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

    public static Long getServeId() {
        return SpringContextHolder.getBean(ClientToGateAcceptor.class).getServeId();
    }


}
