package mmp.im.common.util.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class MQConsumer {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ConnectionFactory connectionFactory = new ConnectionFactory();

    protected Connection connection = null;

    // 队列的消费者，从其中读取消息
    private String consumeFromQueue;


    public MQConsumer(String mqURI, String consumeFromQueue) {

        this.consumeFromQueue = consumeFromQueue;

        try {
            this.connectionFactory.setUri(mqURI);
        } catch (Exception e) {
            LOG.error("init MQConsumer Exception...", e);
        }

        this.connectionFactory.setAutomaticRecoveryEnabled(true);
        this.connectionFactory.setTopologyRecoveryEnabled(false);
        this.connectionFactory.setNetworkRecoveryInterval(5000);
        this.connectionFactory.setRequestedHeartbeat(30);
        this.connectionFactory.setConnectionTimeout(30 * 1000);
    }


    public synchronized void start() {

        Connection conn = this.tryGetConnection();
        if (conn != null) {
            this.startProcessor(conn);
        } else {
            // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
            try {
                Thread.sleep(5 * 1000);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
            new Thread(MQConsumer.this::start);
        }
    }

    private Connection tryGetConnection() {
        if (this.connection == null) {
            try {
                this.connection = this.connectionFactory.newConnection();

                this.connection.addShutdownListener((shutdownSignalException) -> LOG.warn("shutdownCompleted... {}", shutdownSignalException.getReason()));

                // 自动恢复
                ((Recoverable) this.connection).addRecoveryListener(new RecoveryListener() {
                    @Override
                    public void handleRecoveryStarted(Recoverable recoverable) {
                        LOG.warn("handleRecoveryStarted...");
                    }

                    @Override
                    public void handleRecovery(Recoverable recoverable) {
                        MQConsumer.this.start();
                    }
                });
            } catch (Exception e) {
                LOG.error("tryGetConnection Exception...", e);
                this.connection = null;
            }
        }

        return this.connection;
    }

    private void startProcessor(Connection connection) {

        if (this.consumeFromQueue == null || this.consumeFromQueue.equals("")) {
            return;
        }

        try {
            final Channel consumeChannel = connection.createChannel();

            // 取消自动ack
            consumeChannel.basicConsume(this.consumeFromQueue, false, new DefaultConsumer(consumeChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    boolean ok = MQConsumer.this.process(body);
                    // 返回false则消息将被MQ服务器重新放入队列
                    if (ok) {
                        consumeChannel.basicAck(envelope.getDeliveryTag(), false);
                    } else {
                        consumeChannel.basicReject(envelope.getDeliveryTag(), true);
                    }
                }
            });

            LOG.warn("startProcessor...");

        } catch (Exception e) {
            LOG.error("startProcessor Exception...", e);

            try {
                Thread.sleep(5 * 1000);
            } catch (Exception ee) {
                LOG.error(ee.getMessage());
            }
            new Thread(() -> startProcessor(MQConsumer.this.connection));

        }
    }

    protected abstract boolean process(byte[] contentBody);
}
