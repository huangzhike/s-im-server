package mmp.im.common.server.tcp.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventExecutor implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    private IEventListener listener;

    public EventExecutor(IEventListener listener) {
        this.listener = listener;
    }

    public void putNettyEvent(Event event) {
        if (this.eventQueue.size() <= 10000) {
            LOG.warn("putNettyEvent -> {}", event);
            this.eventQueue.offer(event);
        }
    }

    @Override
    public void run() {

        while (true) {
            Event event = null;
            try {
                event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (event != null && listener != null) {
                switch (event.getType()) {
                    case IDLE:
                        listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                        break;
                    case CLOSE:
                        listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                        break;
                    case CONNECT:
                        listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                        break;
                    default:
                        break;
                }
            }

        }
    }


}
