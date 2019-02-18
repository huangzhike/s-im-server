package mmp.im.logic.util;

import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.logic.service.FriendMessageService;
import mmp.im.logic.service.GroupMessageService;
import mmp.im.logic.service.SessionService;
import mmp.im.logic.service.XService;

public class ContextHolder {

    public static XService getXService() {
        return SpringContextHolder.getBean(XService.class);
    }

    public static SessionService getSessionService() {
        return SpringContextHolder.getBean(SessionService.class);
    }

    public static GroupMessageService getGroupMessageService() {
        return SpringContextHolder.getBean(GroupMessageService.class);
    }

    public static FriendMessageService getFriendMessageService() {
        return SpringContextHolder.getBean(FriendMessageService.class);
    }
}
