package mmp.im.common.server.cache.acknowledge;

import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ResendMessageThread implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private ResendMessageMap resendMessageMap;

    public ResendMessageThread(ResendMessageMap resendMessageMap) {
        this.resendMessageMap = resendMessageMap;
    }

    @Override
    public void run() {

        if (this.resendMessageMap == null) {
            return;
        }

        Map<Long, ResendMessage> messageToAckMap = this.resendMessageMap.getMap();

        while (true) {

            for (ResendMessage resendMessage : messageToAckMap.values()) {

                // 五分钟没发成功，删除
                if (System.currentTimeMillis() - resendMessage.getTimestamp() > MINUTES.toMillis(5)) {
                    continue;
                }

                // 10秒没确认
                if (System.currentTimeMillis() - resendMessage.getLastSendTimeStamp() > SECONDS.toMillis(10)) {
                    // 通道未关闭
                    if (resendMessage.getChannelHandlerContext().channel().isActive()) {

                        LOG.warn("重发... {}", resendMessage.getMsg());
                        // 重发
                        resendMessage.getChannelHandlerContext()
                                .channel()
                                .writeAndFlush(resendMessage.getMsg())
                                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                        // 更新时间戳
                        resendMessage.setLastSendTimeStamp(System.currentTimeMillis());
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
