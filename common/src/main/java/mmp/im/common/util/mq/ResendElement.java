package mmp.im.common.util.mq;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResendElement {

    // 交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列
    private String exchangeName;

    // 队列映射的路由key
    private String routingKey;

    private Object msg;

    public ResendElement(Object msg) {

        this.msg = msg;
    }


    public ResendElement(String exchangeName, String routingKey, Object msg) {
        this.exchangeName = exchangeName; this.routingKey = routingKey; this.msg = msg;
    }

}
