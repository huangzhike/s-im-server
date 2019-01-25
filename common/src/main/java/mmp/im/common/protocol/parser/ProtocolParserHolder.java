package mmp.im.common.protocol.parser;

import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProtocolParserHolder {


    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private HashMap<Integer, IProtocolParser> parsers = new HashMap<>();


    public ProtocolParserHolder(String packageName, Class aClass) {
        List<Class<?>> classList = PackageUtil.getSubClasses(packageName, aClass);
        Iterator iterator = classList.iterator();
        while (iterator.hasNext()) {
            Class c = (Class) iterator.next();
            try {
                IProtocolParser instance = (IProtocolParser) c.newInstance();
                parsers.put(instance.getProtocolKind(), instance);
            } catch (Exception e) {
                LOG.error("newInstance Exception... {}", e);
            }
        }

        LOG.warn("parsers... {}", parsers);
        LOG.warn("parsers size... {}", parsers.size());
    }


    private HashMap<Integer, IProtocolParser> getParsers() {

        return parsers;
    }


    public IProtocolParser get(int protocolKind) {
        return getParsers().get(protocolKind);
    }

}
