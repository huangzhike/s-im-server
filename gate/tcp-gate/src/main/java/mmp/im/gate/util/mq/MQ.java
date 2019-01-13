package mmp.im.gate.util.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
    1）一个消息生产者；
    2）一个消息消费者；
    3）与MQ服务器的断线重连和恢复；
    4）发送出错暂存到缓存数组（内存中），并在下次重连正常时自动重发；
    5）复用同一个连接（connection）、各自两个channel（生产者、消费者）；
    6）消费者手动ACK：业务层处理不成功可重新放回队列。
*/
public class MQ {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ConnectionFactory connectionFactory = new ConnectionFactory();
    private Connection connection = null;
    private Channel pubChannel = null;


    // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
    private final Timer startAgainTimer = new Timer();

    // 防止首次连接失败重试时因TimeTask的异步执行而发生重复执行的可能
    private boolean startRunning = false;


    // processor 启动或运行出错时，可以自动恢复
    private final Timer retryProcessorTimer = new Timer();

    // 防止 processor 失败重试时因TimeTask的异步执行发生重复执行
    private boolean retryProcessorRunning = false;

    // 发送消息失败，下次连接恢复时再发送
    private final ConcurrentLinkedQueue<ResendElement> resendQueue = new ConcurrentLinkedQueue<>();


    // 队列的生产者，将消息发送至此
    private String publishToQueue;

    // 队列的消费者，从其中读取消息
    private String consumeFromQueue;


    public MQ(String mqURI, String publishToQueue, String consumeFromQueue) {

        this.publishToQueue = publishToQueue;
        this.consumeFromQueue = consumeFromQueue;

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
                this.startProcessor(conn);
            } else {
                // 重新启动
                this.startAgainTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MQ.this.start();
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
                        MQ.this.start();
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
            ResendElement resendElement = resendQueue.poll();
            this.publish(resendElement.exchangeName, resendElement.routingKey, resendElement.bytes);
        }
    }

    // 发布
    public boolean publish(String exchangeName, String routingKey, byte[] bytes) {
        boolean ok = false;

        try {
            pubChannel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, bytes);
            ok = true;
        } catch (Exception e) {
            resendQueue.add(new ResendElement(exchangeName, routingKey, bytes));
        }
        return ok;
    }


    class ResendElement {
        public String exchangeName;
        public String routingKey;
        public byte[] bytes;

        ResendElement(String exchangeName, String routingKey, byte[] bytes) {
            this.exchangeName = exchangeName;
            this.routingKey = routingKey;
            this.bytes = bytes;
        }
    }

    private void startProcessor(Connection connection) {
        if (this.retryProcessorRunning)
            return;
        String queueName = this.consumeFromQueue; // queue name
        if (queueName == null || queueName.equals("")) {
            return;
        }

        try {
            final Channel resumeChannel = connection.createChannel();

            DefaultConsumer dc = new DefaultConsumer(resumeChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String routingKey = envelope.getRoutingKey();
                    String contentType = properties.getContentType();

                    long deliveryTag = envelope.getDeliveryTag();

                    boolean ok = MQ.this.process(body);
                    // 返回false则消息将被MQ服务器重新放入队列
                    if (ok) {
                        resumeChannel.basicAck(deliveryTag, false);
                    } else {
                        resumeChannel.basicReject(deliveryTag, true);
                    }
                }
            };

            boolean autoAck = false;
            // 取消自动ack
            resumeChannel.basicConsume(queueName, autoAck, dc);

        } catch (Exception e) {

            this.retryProcessorTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startProcessor(MQ.this.connection);
                }
            }, 5 * 1000);
        } finally {
            this.retryProcessorRunning = false;
        }
    }

    protected boolean process(byte[] contentBody) {
        try {
            System.out.println(contentBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
