package mmp.im.common.protocol;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ParserPacket {

    private byte protocolType;

    private byte[] body;

}
