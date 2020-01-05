package mmp.im.common.util.seq;

import java.util.concurrent.atomic.AtomicLong;

public class SeqGenerator {


    /*
    * 数据库实现自增ID
    * UUID
    * Redis来生成ID 依赖于Redis是单线程的 用Redis的原子操作 INCR和INCRBY来实现
    * Redis集群来获取更高的吞吐量。假如一个集群中有5台Redis。可以初始化每台Redis的值分别是1,2,3,4,5，然后步长都是5。各个Redis生成的ID为：
    *A：1,6,11,16,21

B：2,7,12,17,22

C：3,8,13,18,23

D：4,9,14,19,24

E：5,10,15,20,25

这个，随便负载到哪个机确定好 ，未来很难做修改
    *
    *
    * snowflake算法 其核心思想是：使用41bit作为毫秒数，10bit作为机器的ID（5个bit是数据中心，5个bit的机器ID），12bit作为毫秒内的流水号（意味着每个节点在每毫秒可以产生 4096 个 ID），最后还有一个符号位，永远是0。
    * */

    // 可以用redis incr命令实现自增
    private static final AtomicLong seq = new AtomicLong(0);

    public static long get() {
        return seq.getAndIncrement();
    }
}
