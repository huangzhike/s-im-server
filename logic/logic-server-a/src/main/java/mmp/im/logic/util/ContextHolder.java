package mmp.im.logic.util;

import im.database.service.FriendMessageService;
import im.database.service.GroupMessageService;
import im.database.service.SessionService;
import im.database.service.XService;
import mmp.im.common.util.spring.SpringContextHolder;
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
