package mmp.im.gate.configuration;


import im.database.service.FriendMessageService;
import im.database.service.GroupMessageService;
import im.database.service.ServerService;
import im.database.service.SessionService;
import mmp.im.common.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;


    @Bean
    public RedisUtil redisUtil() {
        RedisUtil redisUtil = new RedisUtil();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port);
        redisUtil.setJedisPool(jedisPool);
        return redisUtil;
    }

    @Bean
    public FriendMessageService friendMessageService(RedisUtil redisUtil) {
        return new FriendMessageService(redisUtil);
    }
    @Bean
    public GroupMessageService groupMessageService(RedisUtil redisUtil) {
        return new GroupMessageService(redisUtil);
    }
    @Bean
    public SessionService sessionService(RedisUtil redisUtil) {
        return new SessionService(redisUtil);
    }
    @Bean
    public ServerService serverService(RedisUtil redisUtil) {
        return new ServerService(redisUtil);
    }
}
