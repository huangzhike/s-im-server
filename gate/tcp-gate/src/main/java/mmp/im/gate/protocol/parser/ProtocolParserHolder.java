package mmp.im.gate.protocol.parser;


import mmp.im.common.util.reflect.PackageUtil;
import mmp.im.server.tcp.protocol.parser.IProtocolParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProtocolParserHolder {

    private static HashMap<Integer, Object> parsers;

    private static HashMap getParsers() {
        /*
         * DCL
         * */
        if (parsers == null) {

            synchronized (ProtocolParserHolder.class) {
                if (parsers == null) {
                    parsers = new HashMap<>();
                    List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.gate.protocol.parser", IProtocolParser.class);
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
        }
        return parsers;
    }


    public static IProtocolParser get(int protocolKind) {
        return (IProtocolParser) getParsers().get(protocolKind);
    }
}
