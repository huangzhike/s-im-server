package mmp.im.common.protocol;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ParserPacket {

    private byte protocolType;

    private byte[] body;


    public byte getProtocolType() {
        return protocolType;
    }

    public ParserPacket setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public ParserPacket setBody(byte[] body) {
        this.body = body;
        return this;
    }
}
