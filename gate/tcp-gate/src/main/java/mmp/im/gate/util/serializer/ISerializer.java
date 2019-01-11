package mmp.im.gate.util.serializer;


public interface ISerializer {


    /*
     * 序列化
     * */
    <T> byte[] writeObject(T obj);


    /*
     * 反序列化
     * */
    <T> T readObject(byte[] bytes, Class<T> clazz);
}
