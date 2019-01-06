package mmp.im.gate.ack;

import io.netty.channel.ChannelFutureListener;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AckTimeoutScanner implements Runnable {

    @Override
    public void run() {

        Map<Long, MessageToAck> map = MessageToAckMap.getMap();

        while (true) {

            for (MessageToAck messageToAck : map.values()) {
                // 十秒没确认
                if (System.currentTimeMillis() - messageToAck.getTimestamp() > SECONDS.toMillis(10)) {
                    // 通道未关闭
                    if (messageToAck.getChannel().isActive()) {
                        // 重发
                        messageToAck.getChannel().writeAndFlush(messageToAck.getMsg()).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

                        messageToAck.setTimestamp(System.currentTimeMillis());
                    }
                }
            }

            try {
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
