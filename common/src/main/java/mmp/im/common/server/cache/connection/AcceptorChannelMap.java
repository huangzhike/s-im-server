package mmp.im.common.server.cache.connection;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AcceptorChannelMap {

    private final ConcurrentHashMap<Long, ChannelHandlerContext> handlerContextConcurrentHashMap = new ConcurrentHashMap<>();

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    public List<Long> getChannelMapKeyList() {
        Set<Long> keys = this.handlerContextConcurrentHashMap.keySet();

        return new ArrayList<>(keys);
    }

    public ChannelHandlerContext getChannel(Long key) {
        return handlerContextConcurrentHashMap.get(key);
    }


    public ChannelHandlerContext addChannel(Long key, ChannelHandlerContext channelHandlerContext) {

        // 之后重复登录需要踢掉原来的连接
        if (handlerContextConcurrentHashMap.containsKey(key)) {
            LOG.warn("duplicate key... {}", key);
            ChannelHandlerContext ctx = handlerContextConcurrentHashMap.remove(key);
            if (ctx.channel().isOpen()) {
                ctx.channel().close();
            }
        }
        return handlerContextConcurrentHashMap.putIfAbsent(key, channelHandlerContext);

    }

    public ChannelHandlerContext removeChannel(Long key) {
        ChannelHandlerContext channelHandlerContext = handlerContextConcurrentHashMap.remove(key);
        LOG.warn("remove key... {}", key);
        if (channelHandlerContext.channel().isOpen()) {
            LOG.warn("close key... {}", key);
            channelHandlerContext.channel().close();
        }

        return channelHandlerContext;
    }

}
