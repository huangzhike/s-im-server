package mmp.im.server.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractServer {

    /**
     * The native socket transport for Linux using JNI.
     */
    private static final boolean SUPPORT_NATIVE_ET;

    static {
        boolean epoll;
        try {
            Class.forName("io.netty.channel.epoll.Native");
            epoll = true;
        } catch (Throwable e) {
            epoll = false;
        }
        SUPPORT_NATIVE_ET = epoll;
    }

    protected EventLoopGroup initEventLoopGroup(int workerNum, ThreadFactory workerFactory) {
        return SUPPORT_NATIVE_ET ? new EpollEventLoopGroup(workerNum, workerFactory) : new NioEventLoopGroup(workerNum, workerFactory);
    }
}
