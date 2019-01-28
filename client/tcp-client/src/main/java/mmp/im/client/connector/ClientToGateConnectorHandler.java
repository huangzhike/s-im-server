package mmp.im.client.connector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mmp.im.common.protocol.parser.IProtocolParser;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.protocol.util.ParserPacket;
import mmp.im.common.server.cache.connection.ConnectorChannelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;


@Component
@ChannelHandler.Sharable
public class ClientToGateConnectorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Autowired
    @Qualifier("gateToDistConnectorProtocolParserHolder")
    private ProtocolParserHolder protocolParserHolder;


    @Autowired
    private ConnectorChannelHolder connectorChannelHolder;



}

