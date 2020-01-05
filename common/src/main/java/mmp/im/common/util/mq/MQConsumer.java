package mmp.im.common.util.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class MQConsumer {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ConnectionFactory connectionFactory = new ConnectionFactory();

    protected Connection connection = null;

    protected Channel consumeChannel = null;

    // 队列的消费者，从其中读取消息
    private String consumeFromQueue;

    public MQConsumer(String mqURI, String consumeFromQueue) {

        this.consumeFromQueue = consumeFromQueue;

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
            this.startProcessor(conn);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
            // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
            this.sleep(5 * 1000);
            new Thread(MQConsumer.this::start);
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
                    MQConsumer.this.start();
                }
            });
        }

        return this.connection;
    }

    private void startProcessor(Connection connection) throws Exception {

        this.consumeChannel = connection.createChannel();

        // 取消自动ack
        this.consumeChannel.basicConsume(this.consumeFromQueue, false, new DefaultConsumer(this.consumeChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                boolean ok = MQConsumer.this.process(body);

                if (ok) {
                    consumeChannel.basicAck(envelope.getDeliveryTag(), false);
                }
                // 重新放入队列
                else {
                    consumeChannel.basicReject(envelope.getDeliveryTag(), true);
                }
            }
        });


    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    protected abstract boolean process(byte[] contentBody);
}
