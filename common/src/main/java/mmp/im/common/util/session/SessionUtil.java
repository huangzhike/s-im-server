package mmp.im.common.util.session;

public class SessionUtil {

    public static String getSessionId(String a, String b) {

        int i = a.compareTo(b);

        String result;

        if (i > 0) {
            result = a + "@@@>@@@" + b;
        } else if (i < 0) {
            result = a + "@@@<@@@" + b;
        } else {
            result = a + "@@@=@@@" + b;
        }

        return result;
    }

}
