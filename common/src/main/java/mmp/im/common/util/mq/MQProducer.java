package mmp.im.common.util.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class MQProducer {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    // 发送消息失败，下次连接恢复时再发送
    protected final LinkedBlockingQueue<ResendElement> resendQueue = new LinkedBlockingQueue<>();

    private final ConnectionFactory connectionFactory = new ConnectionFactory();

    protected Connection connection = null;

    protected Channel pubChannel = null;

    // 队列的生产者，将消息发送至此
    protected String publishToQueue;
    protected String exchange;
    protected String routingKey;


    public MQProducer(String mqURI, String exchange, String routingKey, String publishToQueue) {

        this.publishToQueue = publishToQueue;
        this.exchange = exchange;
        this.routingKey = routingKey;

        try {
            this.connectionFactory.setUri(mqURI);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
        }

        this.connectionFactory.setAutomaticRecoveryEnabled(true);
        this.connectionFactory.setTopologyRecoveryEnabled(false);
        this.connectionFactory.setNetworkRecoveryInterval(5000);
        this.connectionFactory.setRequestedHeartbeat(30);
        this.connectionFactory.setConnectionTimeout(30 * 1000);

    }


    public synchronized void start() {


        try {
            Connection conn = this.tryGetConnection();
            this.startPublisher(conn);
        } catch (Exception e) {

            LOG.error(e.getLocalizedMessage());
            // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
            this.sleep(5 * 1000);
            new Thread(MQProducer.this::start);
        }

        LOG.warn("start...");
    }

    private Connection tryGetConnection() throws Exception {
        if (this.connection == null) {
            this.connection = this.connectionFactory.newConnection();

            this.connection.addShutdownListener((shutdownSignalException) -> LOG.warn(shutdownSignalException.getLocalizedMessage()));

            // 自动恢复
            ((Recoverable) this.connection).addRecoveryListener(new RecoveryListener() {
                @Override
                public void handleRecoveryStarted(Recoverable recoverable) {
                    LOG.warn("handleRecoveryStarted...");
                }

                @Override
                public void handleRecovery(Recoverable recoverable) {
                    MQProducer.this.start();
                }
            });
        }

        return this.connection;
    }

    // 开始发布
    private void startPublisher(Connection connection) throws Exception {
        if (this.pubChannel != null && this.pubChannel.isOpen()) {
            this.pubChannel.close();
        }

        this.pubChannel = connection.createChannel();

        // 声明队列
        pubChannel.queueDeclare(this.publishToQueue, true, false, false, null);

        // 绑定队列到交换机
        pubChannel.queueBind(this.publishToQueue, this.exchange, this.routingKey);

        /*
            队列服务：生产者、队列、消费者
            RabbitMQ在生产者和队列之间加入了交换器
            这样生产者和队列就没有直接联系
            生产者把消息给交换器，交换器根据调度策略再把消息再给队列
        */


        ResendElement resendElement;
        while ((resendElement = resendQueue.poll()) != null) {

            this.publish(resendElement.getMsg());
        }
    }


    protected void addToResend(Object contents) {
        ResendElement resendElement = new ResendElement(contents);

        this.resendQueue.offer(resendElement);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    public abstract boolean publish(Object msg);

}
