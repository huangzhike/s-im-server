package mmp.im.common.util.redis;

import lombok.Data;
import lombok.experimental.Accessors;
import mmp.im.common.util.serializer.IOSerializer;
import mmp.im.common.util.serializer.SerializerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;


@Data
@Accessors(chain = true)
public class RedisUtil {


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private JedisPool jedisPool;

    // *******
    public <T> T getMapValue(String mapkey, String key, Class<T> requiredType) {

        try (Jedis jedis = jedisPool.getResource()) {
            byte[] result = jedis.hget(mapkey.getBytes(), key.getBytes());
            return SerializerHolder.getSerializer().deserialize(result, requiredType);
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return null;
    }


    public void addMapValue(String mapkey, String key, Object value) {

        try (Jedis jedis = jedisPool.getResource()) {
            byte[] keyValue = SerializerHolder.getSerializer().serialize(value);
            jedis.hset(mapkey.getBytes(), key.getBytes(), keyValue);
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
    }

    public Long removeMapValue(String mapKey, String... dkey) {
        Long result = null;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = dkey[i].getBytes();
            }
            result = jedis.hdel(mapKey.getBytes(), dx);
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return result;
    }


    public <T> Map<String, T> getMap(String mapkey, Class<T> requiredType) {

        Map<String, T> map = new HashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Map<byte[], byte[]> result = jedis.hgetAll(mapkey.getBytes());
            result.forEach((k, v) -> map.put(new String(k), SerializerHolder.getSerializer().deserialize(v, requiredType)));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return map;
    }


    // *******

    public <T> List<T> getList(String listKey, int start, int end, Class<T> requiredType) {
        List<T> list = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            List<byte[]> xx = jedis.lrange(listKey.getBytes(), start, end);
            xx.forEach(bytes -> list.add(SerializerHolder.getSerializer().deserialize(bytes, requiredType)));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return list;
    }

    public <T> List<T> getList(String listKey, Class<T> requiredType) {
        return this.getList(listKey, 0, -1, requiredType);
    }

    public void addListValue(String listKey, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.lpush(listKey.getBytes(), SerializerHolder.getSerializer().serialize(value));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }

    }

    // *******
    public void addSetValue(String key, Object value) {

        try (Jedis jedis = jedisPool.getResource()) {
            long status = jedis.sadd(SerializerHolder.getSerializer().serialize(key), SerializerHolder.getSerializer().serialize(value));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }

    }

    public Long removeSetValue(String key, Object value) {

        Long result = null;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.srem(key.getBytes(), SerializerHolder.getSerializer().serialize(value));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return result;

    }

    public <T> Set<T> getSet(String key, Class<T> clazz) {

        Set<T> hashSet = new HashSet<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<byte[]> bytes = jedis.smembers(SerializerHolder.getSerializer().serialize(key));
            Optional.ofNullable(bytes).orElse(new HashSet<>()).forEach(b -> hashSet.add(SerializerHolder.getSerializer().deserialize(b, clazz)));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return hashSet;

    }


    // *******

    public void addSortedSetValue(String key, Long id, Object object) {

        try (Jedis jedis = jedisPool.getResource()) {
            byte[] bytes = SerializerHolder.getSerializer().serialize(object);
            jedis.zadd(key.getBytes(), id, bytes);
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
    }


    public <T> Set<T> getSortedSet(String key, Long start, Long end, Class<T> clazz) {
        Set<T> hashSet = new HashSet<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<byte[]> set = jedis.zrange(key.getBytes(), start, end);
            Optional.ofNullable(set).orElse(new HashSet<>()).forEach(bytes -> hashSet.add(SerializerHolder.getSerializer().deserialize(bytes, clazz)));
        } catch (Exception e) {
            LOG.error("Exception... {}", e);
        }
        return hashSet;
    }

    // *******

    public String hget(String hkey, String key) {
        Jedis jedis = jedisPool.getResource();
        String string = jedis.hget(hkey, key);
        jedis.close();
        return string;
    }

    public long hset(String hkey, String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hset(hkey, key, value);
        jedis.close();
        return result;
    }

    public long hdel(String hkey, String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hdel(hkey, key);
        jedis.close();
        return result;
    }

}
