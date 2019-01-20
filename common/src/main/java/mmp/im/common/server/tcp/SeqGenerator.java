package mmp.im.common.server.tcp;

import java.util.concurrent.atomic.AtomicLong;

public class SeqGenerator {

    private static final AtomicLong seqGenerator = new AtomicLong();

    public static Long get() {
        return seqGenerator.incrementAndGet();
    }
}
