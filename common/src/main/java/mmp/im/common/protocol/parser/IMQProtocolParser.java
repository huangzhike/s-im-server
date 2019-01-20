package mmp.im.common.protocol.parser;

public interface IMQProtocolParser {
    int getProtocolKind();

    void parse(byte[] bytes);

}
