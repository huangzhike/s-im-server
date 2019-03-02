package mmp.im.common.protocol.util;

public abstract class ProtocolHeader {

    public static final int FLAG_NUM = 0xbabe;
    public static final int HEAD_LENGTH = 4 + 1 + 2;

    protected int flag = FLAG_NUM;

    protected byte commandId;

    protected short bodyLength;

}
