package mmp.im.common.protocol.handler;

import com.google.protobuf.MessageLite;
import mmp.im.common.util.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandlerHolder {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<String, IMessageHandler> messageHandlers = new ConcurrentHashMap<>();

    public MessageHandlerHolder(String packageName, Class aClass) {

        List<Class<?>> classList = PackageUtil.getSubClasses(packageName, aClass);

        classList.forEach(v -> {
            try {
                IMessageHandler instance = (IMessageHandler) v.newInstance();
                this.messageHandlers.put(instance.getHandlerName(), instance);
            } catch (Exception e) {
                LOG.error("newInstance Exception...", e);
            }
        });

    }

    private Map<String, IMessageHandler> getMessageHandlers() {
        return this.messageHandlers;
    }

    public void assignHandler(MessageLite message) {

        String name = message.getClass().toString();

        IMessageHandler handler = this.getMessageHandlers().get(name);

        if (handler != null) {
            handler.process(message);
        }

    }
}
