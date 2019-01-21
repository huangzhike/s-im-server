package mmp.im.logic.protocol.parser;


import mmp.im.common.protocol.parser.IMQProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProtocolParserHolder {

    private static HashMap<Integer, IMQProtocolParser> parsers;

    static {

        parsers = new HashMap<>();

        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.logic.protocol.parser", IMQProtocolParser.class);

        Iterator iterator = classList.iterator();

        while (iterator.hasNext()) {
            Class c = (Class) iterator.next();
            try {
                IMQProtocolParser instance = (IMQProtocolParser) c.newInstance();
                parsers.put(instance.getProtocolKind(), instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static HashMap<Integer, IMQProtocolParser> getParsers() {

        return parsers;
    }


    public static IMQProtocolParser get(int protocolKind) {
        return getParsers().get(protocolKind);
    }
}
