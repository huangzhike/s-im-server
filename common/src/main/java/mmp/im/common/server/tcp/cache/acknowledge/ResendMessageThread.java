package mmp.im.common.server.tcp.cache.acknowledge;

import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ResendMessageThread implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {

        Map<Long, ResendMessage> map = ResendMessageMap.getMap();

        while (true) {

            for (ResendMessage resendMessage : map.values()) {
                // 10秒没确认
                if (System.currentTimeMillis() - resendMessage.getTimestamp() > SECONDS.toMillis(10)) {
                    // 通道未关闭
                    if (resendMessage.getChannelHandlerContext().channel().isActive()) {

                        LOG.warn("重发... {}", resendMessage.getMsg());
                        // 重发
                        resendMessage.getChannelHandlerContext().channel().writeAndFlush(resendMessage.getMsg()).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                        // 更新时间戳
                        resendMessage.setTimestamp(System.currentTimeMillis());
                    }
                }
            }

            try {
                Thread.sleep(300);
            } catch (Exception e) {
                LOG.error("Thread Exception... {}", e);
            }

        }

    }
}
