package mmp.im.common.util.serializer;


public interface ISerializer {


    /*
     * 序列化
     * */
    <T> byte[] serialize(T obj);


    /*
     * 反序列化
     * */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
