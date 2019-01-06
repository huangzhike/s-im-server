package mmp.im.gate.util.serializer;


public final class SerializerHolder {

    private static final ISerializer serializer = new ProtoStuffSerializer();

    public static ISerializer getSerializer() {
        return serializer;
    }
}
