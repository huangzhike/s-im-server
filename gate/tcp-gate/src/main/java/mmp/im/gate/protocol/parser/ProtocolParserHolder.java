package mmp.im.gate.protocol.parser;


import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProtocolParserHolder {

    private static HashMap<Integer, IProtocolParser> parsers;

    private static HashMap<Integer, IProtocolParser> getParsers() {
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
                            IProtocolParser instance = (IProtocolParser) c.newInstance();
                            parsers.put(instance.getProtocolKind(), instance);
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
        return getParsers().get(protocolKind);
    }
}
