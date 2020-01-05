package mmp.im.gate.acceptor;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ChannelHandler.Sharable
public class ClientToGateAcceptorAuthHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
    // CompositeByteBuf实现零拷贝
    // 登陆验证，之后将自己移除
    }

}
