package mmp.im.common.server.tcp.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractServer implements IServer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractServer.class);

    /**
     * The native socket transport for Linux using JNI.
     */
    private static final boolean SUPPORT_NATIVE_ET;

    /*
     * Windows不支持epoll模型
     * */
    static {
        boolean epoll;
        try {
            Class.forName("io.netty.channel.epoll.Native");
            epoll = true;
        } catch (Throwable e) {
            epoll = false;
        }
        SUPPORT_NATIVE_ET = epoll;

        LOG.warn("SUPPORT_NATIVE_ET epoll... {}", epoll);
    }

    protected EventLoopGroup initEventLoopGroup(int workerNum, ThreadFactory workerFactory) {
        return SUPPORT_NATIVE_ET ? new EpollEventLoopGroup(workerNum, workerFactory) : new NioEventLoopGroup(workerNum, workerFactory);
    }
}
