package mmp.im.auth.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisUtil {



    @Autowired
    private JedisPool jedisPool;


    @SuppressWarnings("unchecked")
    public <T> T getMapValue(String mapkey, String key, Class<T> requiredType)  {

        try (Jedis jedis = jedisPool.getResource()) {
            List<byte[]> result = jedis.hmget(mapkey.getBytes(), key.getBytes());
            if (null != result && result.size() > 0) {
                byte[] x = result.get(0);
                T resultObj = SerializeUtil.deserialize(x, requiredType);
                return resultObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setMapValue(String mapkey, String key, Object value)   {

        try (Jedis jedis = jedisPool.getResource()) {
            byte[] keyValue = SerializeUtil.serialize(value);
            jedis.hset(mapkey.getBytes(), key.getBytes(), keyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object delMapValue(String mapKey, String... dkey)  {
        Long result = null;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = dkey[i].getBytes();
            }
            result = jedis.hdel(mapKey.getBytes(), dx);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String listKey, int start, int end, Class<T> requiredType)  {
        List<T> list = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {

            List<byte[]> xx = jedis.lrange(listKey.getBytes(), start, end);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String listKey, Class<T> requiredType)  {
        List<T> list = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {

            // 取全部
            List<byte[]> xx = jedis.lrange(listKey.getBytes(), 0, -1);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void addList(String listKey, Object value)  {

        try (Jedis jedis = jedisPool.getResource()) {

            byte[] listItem = SerializeUtil.serialize(value);
            jedis.lpush(listKey.getBytes(), listItem);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


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
