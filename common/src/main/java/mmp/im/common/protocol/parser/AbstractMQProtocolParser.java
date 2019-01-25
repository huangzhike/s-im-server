package mmp.im.common.protocol.parser;

import mmp.im.common.protocol.handler.IMQMessageTypeHandler;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMQProtocolParser {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<String, IMQMessageTypeHandler> msgTypeHandlers = new HashMap<>();

    protected void initHandler(String packageName, Class aClass) {

        List<Class<?>> classList = PackageUtil.getSubClasses(packageName, aClass);

        classList.forEach(v -> {
            try {
                IMQMessageTypeHandler e = (IMQMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                LOG.error("newInstance Exception... {}", e);
            }
        });

    }

    private Map<String, IMQMessageTypeHandler> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }

    protected void assignHandler(String type, Object message) {

        IMQMessageTypeHandler handler = this.getMsgTypeHandlers().get(type);
        if (handler != null) {
            handler.process(message);
        }

    }

}
