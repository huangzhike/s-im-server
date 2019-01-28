package mmp.im.gate.acceptor;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mmp.im.common.protocol.parser.ProtocolParserHolder;
import mmp.im.common.server.cache.connection.AcceptorChannelHandlerMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@ChannelHandler.Sharable
// public class InboundHandlerHandler extends SimpleChannelInboundHandler<Object> {
public class ClientToGateAcceptorHandler extends ChannelInboundHandlerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("clientToGateAcceptorProtocolParserHolder")
    private ProtocolParserHolder protocolParserHolder;

    @Autowired
    private AcceptorChannelHandlerMap acceptorChannelHandlerMap;



}
