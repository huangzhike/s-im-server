package mmp.im.auth.protocol;


import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class ProtocolParserHolder {

    private static HashMap<Integer, IProtocolParser> parsers;

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolParserHolder.class);


    static {

        parsers = new HashMap<>();
        List<Class<?>> classList = PackageUtil.getSubClasses("mmp.im.auth.protocol.parser", IProtocolParser.class);

        if (classList != null) {
            classList.forEach(aClass -> {
                try {
                    IProtocolParser e = (IProtocolParser) aClass.newInstance();
                    parsers.put(e.getProtocolKind(), e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        LOG.warn("parsers -> {}", parsers);
        LOG.warn("parsers size -> {}", parsers.size());

    }

    private static HashMap<Integer, IProtocolParser> getParsers() {

        return parsers;
    }


    public static IProtocolParser get(int protocolKind) {
        return getParsers().get(protocolKind);
    }
}
