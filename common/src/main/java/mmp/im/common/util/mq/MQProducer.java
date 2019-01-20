package mmp.im.common.util.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class MQProducer {
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected final ConnectionFactory connectionFactory = new ConnectionFactory();
    protected Connection connection = null;
    protected Channel pubChannel = null;


    // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
    protected final Timer startAgainTimer = new Timer();

    // 防止首次连接失败重试时因TimeTask的异步执行而发生重复执行的可能
    protected boolean startRunning = false;

    // 发送消息失败，下次连接恢复时再发送
    protected final ConcurrentLinkedQueue<ResendElement> resendQueue = new ConcurrentLinkedQueue<>();


    // 队列的生产者，将消息发送至此
    protected String publishToQueue;


    public MQProducer(String mqURI, String publishToQueue) {

        this.publishToQueue = publishToQueue;


        try {
            // 消息队列服务器连接URI，如：“amqp://admin:123456789@192.168.1.190”
            this.connectionFactory.setUri(mqURI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.connectionFactory.setAutomaticRecoveryEnabled(true);
        this.connectionFactory.setTopologyRecoveryEnabled(false);
        this.connectionFactory.setNetworkRecoveryInterval(5000);
        this.connectionFactory.setRequestedHeartbeat(30);
        this.connectionFactory.setConnectionTimeout(30 * 1000);
    }


    public void start() {
        if (this.startRunning) {
            return;
        }

        try {
            Connection conn = this.tryGetConnection();
            if (conn != null) {
                this.startPublisher(conn);

            } else {
                // 重新启动
                this.startAgainTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MQProducer.this.start();
                    }
                }, 5 * 1000);
            }
        } finally {
            this.startRunning = false;
        }
    }

    private Connection tryGetConnection() {
        if (this.connection == null) {
            try {
                this.connection = this.connectionFactory.newConnection();

                this.connection.addShutdownListener((shutdownSignalException) -> {
                    LOG.warn("shutdownCompleted: " + shutdownSignalException.getReason().toString());
                });

                // 自动恢复
                ((Recoverable) this.connection).addRecoveryListener(new RecoveryListener() {
                    @Override
                    public void handleRecoveryStarted(Recoverable recoverable) {
                        LOG.warn("handleRecoveryStarted");
                    }

                    @Override
                    public void handleRecovery(Recoverable recoverable) {
                        MQProducer.this.start();
                    }
                });
            } catch (Exception e) {

                this.connection = null;

            }
        }

        return this.connection;
    }

    // 开始发布
    private void startPublisher(Connection connection) {
        if (this.pubChannel != null && this.pubChannel.isOpen()) {
            try {
                this.pubChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String queue = this.publishToQueue; // queue name
        boolean durable = true; // durable - RabbitMQ will never lose the queue if a crash occurs
        boolean exclusive = false; // exclusive - if queue only will be used by one connection
        boolean autoDelete = false; // autodelete - queue is deleted when last consumer unsubscribes

        if (queue == null || queue.equals("")) {
            return;
        }

        try {
            this.pubChannel = connection.createChannel();
            // 声明队列
            AMQP.Queue.DeclareOk queueDeclare = pubChannel.queueDeclare(queue, durable, exclusive, autoDelete, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        while (resendQueue.size() > 0) {
            MQProducer.ResendElement resendElement = resendQueue.poll();
            this.publish(resendElement.exchangeName, resendElement.routingKey, resendElement.msg);
        }
    }

    // 发布
    public abstract boolean publish(String exchangeName, String routingKey, Object msg);

    /*
        队列服务： 发消息者、队列、收消息者
        RabbitMQ在发消息者和 队列之间, 加入了交换器 (Exchange)
        这样发消息者和队列就没有直接联系, 转而变成发消息者把消息给交换器, 交换器根据调度策略再把消息再给队列
     */
    protected class ResendElement {
        // 交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列

        public String exchangeName;
        // 队列映射的路由key
        public String routingKey;
        public Object msg;

        public ResendElement(String exchangeName, String routingKey, Object msg) {
            this.exchangeName = exchangeName;
            this.routingKey = routingKey;
            this.msg = msg;
        }
    }
}
