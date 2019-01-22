package mmp.im.common.util.mq;

public class ResendElement {

    // 交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列
    private String exchangeName;

    // 队列映射的路由key
    private String routingKey;

    private Object msg;

    public ResendElement(String exchangeName, String routingKey, Object msg) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.msg = msg;
    }

    public String getExchangeName() {
        return exchangeName;
    }
    public ResendElement setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return this;
    }
    public String getRoutingKey() {
        return routingKey;
    }
    public ResendElement setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }
    public Object getMsg() {
        return msg;
    }
    public ResendElement setMsg(Object msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "ResendElement{" +
                "exchangeName='" + exchangeName + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", msg=" + msg +
                '}';
    }
}
