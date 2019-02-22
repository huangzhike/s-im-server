package mmp.im.common.util;

public class SessionUtil {

    public static String getSessionId(Long a, Long b) {
        long max = Math.max(a, b);
        long min = Math.min(a, b);
        return (min + "@@@" + max);
    }

}
