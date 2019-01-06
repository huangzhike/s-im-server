package mmp.im.gate.protocol.parser;


import mmp.im.gate.util.PackageUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProtocolParserHolder {

    private static HashMap<Integer, Object> parsers;

    private static HashMap getParsers() {
        if (parsers == null) {
            init();
        }
        return parsers;
    }

    /*
     * DCL
     * */
    private static synchronized void init() {
        if (parsers == null) {
            parsers = new HashMap<>();
            List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol", IProtocolParser.class);
            Iterator iterator = classList.iterator();

            while (iterator.hasNext()) {
                Class c = (Class) iterator.next();
                try {
                    IProtocolParser e = (IProtocolParser) c.newInstance();
                    parsers.put(e.getProtocolKind(), e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static IProtocolParser get(int protocolKind) {
        return (IProtocolParser) getParsers().get(protocolKind);
    }
}
