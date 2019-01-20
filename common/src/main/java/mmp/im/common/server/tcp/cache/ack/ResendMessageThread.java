package mmp.im.common.server.tcp.cache.ack;

import io.netty.channel.ChannelFutureListener;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ResendMessageThread implements Runnable {

    @Override
    public void run() {

        Map<Long, ResendMessage> map = ResendMessageMap.getMap();

        while (true) {

            for (ResendMessage resendMessage : map.values()) {
                // 十秒没确认
                if (System.currentTimeMillis() - resendMessage.getTimestamp() > SECONDS.toMillis(10)) {
                    // 通道未关闭
                    if (resendMessage.getChannel().isActive()) {
                        // 重发
                        resendMessage.getChannel().writeAndFlush(resendMessage.getMsg()).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

                        resendMessage.setTimestamp(System.currentTimeMillis());
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
