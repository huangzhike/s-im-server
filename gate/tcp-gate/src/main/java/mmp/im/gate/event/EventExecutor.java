package mmp.im.gate.event;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventExecutor implements Runnable {


    private final LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    private IEventListener listener;

    public EventExecutor(IEventListener listener) {
        this.listener = listener;
    }

    public void putNettyEvent(Event event) {
        if (this.eventQueue.size() <= 10000) {
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
