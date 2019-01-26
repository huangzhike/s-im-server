package mmp.im.common.util.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class MQProducer {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ConnectionFactory connectionFactory = new ConnectionFactory();

    protected Connection connection = null;

    protected Channel pubChannel = null;

    // 队列的生产者，将消息发送至此
    private String publishToQueue;

    // 发送消息失败，下次连接恢复时再发送
    protected final ConcurrentLinkedQueue<ResendElement> resendQueue = new ConcurrentLinkedQueue<>();

    // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
    private final Timer startAgainTimer = new Timer();

    // 防止首次连接失败重试时因TimeTask的异步执行而发生重复执行
    private boolean startRunning = false;

    public MQProducer(String mqURI, String publishToQueue) {

        this.publishToQueue = publishToQueue;

        try {
            this.connectionFactory.setUri(mqURI);
        } catch (Exception e) {
            LOG.error("init MQProducer Exception... {}", e);
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
                    LOG.warn("shutdownCompleted... {}", shutdownSignalException.getReason());
                });

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
            } catch (Exception e) {
                LOG.error("tryGetConnection Exception... {}", e);
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
                LOG.error("startPublisher close Exception... {}", e);
            }
        }

        if (this.publishToQueue == null || this.publishToQueue.equals("")) {
            return;
        }

        try {
            this.pubChannel = connection.createChannel();
            // 声明队列
            AMQP.Queue.DeclareOk queueDeclare = pubChannel.queueDeclare(this.publishToQueue, true, false, false, null);

        } catch (Exception e) {
            LOG.error("startPublisher pubChannel Exception... {}", e);
        }

        while (resendQueue.size() > 0) {
            ResendElement resendElement = resendQueue.poll();
            this.publish(resendElement.getExchangeName(), resendElement.getRoutingKey(), resendElement.getMsg());
        }
    }

    /*
        队列服务： 发消息者、队列、收消息者
        RabbitMQ在发消息者和队列之间, 加入了交换器 (Exchange)
        这样发消息者和队列就没有直接联系, 转而变成发消息者把消息给交换器, 交换器根据调度策略再把消息再给队列
     */

    // 发布
    public abstract boolean publish(String exchangeName, String routingKey, Object msg);

    public abstract boolean pub(Object msg);

}
