package mmp.im.auth.dao;

import mmp.im.auth.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

public class RedisDao {


    @Autowired
    private ShardedJedisPool jedisPool;




    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T>... requiredType) throws Exception {
        // try with resource 多好
        try (ShardedJedis jedis = jedisPool.getResource()) {
            return SerializeUtil.deserialize(jedis.get(key.getBytes()), requiredType);
        } catch (Exception e) {
            throw e;
        }
    }

    public Object getObject(String key) {
        try (ShardedJedis jedis = jedisPool.getResource()) {
            byte[] bytes = jedis.get(key.getBytes());
            // 查询到了，反序列化
            if (bytes != null) {
                return SerializeUtil.deserialize(bytes);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String setObject(String key, Object object) {
        try (ShardedJedis jedis = jedisPool.getResource()) {
            // 序列化
            byte[] bytes = SerializeUtil.serialize(object);
            // 超时缓存
            int timeout = 60 * 60; // 1小时
            return jedis.setex(key.getBytes(), timeout, bytes);
        } catch (Exception e) {
        }
        return null;
    }


    public Long delObject(String key) {
        ShardedJedis jedis = jedisPool.getResource();
        Long result = jedis.del(key.getBytes());
        jedis.close();
        return result;
    }


    @SuppressWarnings("unchecked")
    public <T> T getMapValue(String mapkey, String key, Class<T> requiredType) throws Exception {

        try (ShardedJedis jedis = jedisPool.getResource()) {
            List<byte[]> result = jedis.hmget(mapkey.getBytes(), key.getBytes());
            if (null != result && result.size() > 0) {
                byte[] x = result.get(0);
                T resultObj = SerializeUtil.deserialize(x, requiredType);
                return resultObj;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


    public Object getMapValue(String mapkey, String key) throws Exception {

        try (ShardedJedis jedis = jedisPool.getResource()) {
            List<byte[]> result = jedis.hmget(mapkey.getBytes(), key.getBytes());
            if (null != result && result.size() > 0) {
                byte[] x = result.get(0);

                return SerializeUtil.deserialize(x);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public void setMapValue(String mapkey, String key, Object value) throws Exception {

        try (ShardedJedis jedis = jedisPool.getResource()) {
            byte[] keyValue = SerializeUtil.serialize(value);
            jedis.hset(mapkey.getBytes(), key.getBytes(), keyValue);
        } catch (Exception e) {
            throw e;
        }
    }


    public Object delMapValue(String mapKey, String... dkey) throws Exception {
        try (ShardedJedis jedis = jedisPool.getResource()) {
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = dkey[i].getBytes();
            }
            Long result = jedis.hdel(mapKey.getBytes(), dx);

            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String listKey, int start, int end, Class<T> requiredType) throws Exception {

        try (ShardedJedis jedis = jedisPool.getResource()) {
            List<T> list = new ArrayList<>();
            List<byte[]> xx = jedis.lrange(listKey.getBytes(), start, end);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw e;
        }

    }
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String listKey, Class<T> requiredType) throws Exception {

        try (ShardedJedis jedis = jedisPool.getResource()) {
            List<T> list = new ArrayList<>();
            // 取全部
            List<byte[]> xx = jedis.lrange(listKey.getBytes(), 0, -1);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw e;
        }

    }


    public void addList(String listKey, Object value) throws Exception {

        try (ShardedJedis jedis = jedisPool.getResource()) {

            // jedis.exists(listKey)

            byte[] listItem = SerializeUtil.serialize(value);
            jedis.lpush(listKey.getBytes(), listItem);

        } catch (Exception e) {
            throw e;
        }

    }





    public String hget(String hkey, String key) {
        ShardedJedis jedis = jedisPool.getResource();
        String string = jedis.hget(hkey, key);
        jedis.close();
        return string;
    }

    public long hset(String hkey, String key, String value) {
        ShardedJedis jedis = jedisPool.getResource();
        Long result = jedis.hset(hkey, key, value);
        jedis.close();
        return result;
    }

    public long hdel(String hkey, String key) {
        ShardedJedis jedis = jedisPool.getResource();
        Long result = jedis.hdel(hkey, key);
        jedis.close();
        return result;
    }

      
}
