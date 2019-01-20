package mmp.im.common.util.serializer;


import java.io.*;

@SuppressWarnings("unchecked")
public class IOSerializer {

    // 序列化
    public static byte[] serialize(Object obj) {

        if (obj == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(obj);
            rv = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(oos);
            close(baos);
        }
        return rv;
    }


    // 反序列化
    public static Object deserialize(byte[] bytes) {
        return deserialize(bytes, Object.class);
    }

    public static <T> T deserialize(byte[] bytes, Class<T>... requiredType) {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (bytes != null) {
                bis = new ByteArrayInputStream(bytes);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException("Object can't be deserialized", e);
        } finally {
            close(is);
            close(bis);
        }
        return (T) rv;
    }

    // 关闭流
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("close stream error");
            }
        }
    }
}