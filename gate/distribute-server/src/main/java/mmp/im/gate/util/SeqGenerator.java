package mmp.im.gate.util;

import java.util.concurrent.atomic.AtomicLong;

public class SeqGenerator {

    private static final AtomicLong seq = new AtomicLong(0);

    public static long get() {
        return seq.getAndIncrement();
    }
}
