package mmp.im.common.protocol.parser;

import io.netty.channel.ChannelHandlerContext;
import mmp.im.common.protocol.handler.IMessageTypeHandler;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractProtocolParser {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<String, IMessageTypeHandler> msgTypeHandlers = new HashMap<>();

    protected void initHandler(String packageName, Class aClass) {

        List<Class<?>> classList = PackageUtil.getSubClasses(packageName, aClass);

        classList.forEach(v -> {
            try {
                IMessageTypeHandler e = (IMessageTypeHandler) v.newInstance();
                this.msgTypeHandlers.put(e.getHandlerName(), e);
            } catch (Exception e) {
                LOG.error("newInstance Exception... {}", e);
            }
        });

    }

    private Map<String, IMessageTypeHandler> getMsgTypeHandlers() {
        return this.msgTypeHandlers;
    }

    protected void assignHandler(ChannelHandlerContext channelHandlerContext, String type, Object message) {

        IMessageTypeHandler handler = this.getMsgTypeHandlers().get(type);
        if (handler != null) {
            handler.process(channelHandlerContext, message);
        }

    }


}
