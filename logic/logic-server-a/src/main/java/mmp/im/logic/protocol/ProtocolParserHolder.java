package mmp.im.logic.protocol;


import mmp.im.common.protocol.parser.IMQProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolParserHolder {

    private static Map<Integer, IMQProtocolParser> parsers;

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolParserHolder.class);

    static {

        parsers = new ConcurrentHashMap<>();

        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.logic.protocol.parser", IMQProtocolParser.class);

        Iterator iterator = classList.iterator();

        while (iterator.hasNext()) {
            Class c = (Class) iterator.next();
            try {
                IMQProtocolParser instance = (IMQProtocolParser) c.newInstance();
                parsers.put(instance.getProtocolKind(), instance);
            } catch (Exception e) {
                LOG.error("newInstance Exception... {}", e);
            }
        }

        LOG.warn("parsers... {}", parsers);
        LOG.warn("parsers size... {}", parsers.size());
    }

    private static Map<Integer, IMQProtocolParser> getParsers() {
        return parsers;
    }


    public static IMQProtocolParser get(int protocolKind) {
        return getParsers().get(protocolKind);
    }
}
