package mmp.im.common.util.serializer;


public final class SerializerHolder {

    private static final ISerializer serializer = new ProtoStuffSerializer();

    public static ISerializer getSerializer() {
        return serializer;
    }
}
