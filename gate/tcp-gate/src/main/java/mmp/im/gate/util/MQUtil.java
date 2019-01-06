package mmp.im.gate.util;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;



/*
1）一个消息生产者；
2）一个消息消费者；
3）与MQ服务器的断线重连和恢复；
4）发送出错暂存到缓存数组（内存中），并在下次重连正常时自动重发；
5）复用同一个连接（connection）、各自两个channel（生产者、消费者）；
6）消费者手动ACK能力：业务层处理不成功可重新放回队列。
*/

public class MQUtil {


    private ConnectionFactory connectionFactory;
    private Connection connection = null;
    private Channel pubChannel = null;


    // automaticRecovery只在连接成功后才会启动，首次无法成功建立Connection的需要重新start
    private final Timer startAgainTimer = new Timer();

    // 仅用于防止首次连接失败重试时因TimeTask的异步执行而发生重复执行的可能
    private boolean startRunning = false;


    // 当worker启动或运行过程中出错时，可以自动恢复
    private final Timer retryWorkerTimer = new Timer();

    // 仅用于防止worker失败重试时因TimeTask的异步执行而发生重复执行的可能
    private boolean retryWorkerRunning = false;


    // 发送消息时，可能连接等原因消息没有成功发出，下次连接恢复时，再次由本类自动完成发送，
    private ConcurrentLinkedQueue<String[]> resendQueue = new ConcurrentLinkedQueue<>();


    // 队列的生产者，将消息发送至此
    private String publishToQueue;

    // 队列的消费者，从其中读取消息
    private String consumeFromQueue;


    public MQUtil(String mqURI, String publishToQueue, String consumeFromQueue) {

        this.publishToQueue = publishToQueue;
        this.consumeFromQueue = consumeFromQueue;

        connectionFactory = new ConnectionFactory();
        try {
            // 消息队列服务器连接URI，如：“amqp://admin:123456789@192.168.1.190”
            connectionFactory.setUri(mqURI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setTopologyRecoveryEnabled(false);
        connectionFactory.setNetworkRecoveryInterval(5000);
        connectionFactory.setRequestedHeartbeat(30);
        connectionFactory.setConnectionTimeout(30 * 1000);
    }


    private Connection tryGetConnection() {
        if (connection == null) {
            try {
                connection = connectionFactory.newConnection();
                connection.addShutdownListener(new ShutdownListener() {
                    @Override
                    public void shutdownCompleted(ShutdownSignalException cause) {
                    }
                });

                // 自动恢复
                ((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
                    @Override
                    public void handleRecoveryStarted(Recoverable recoverable) {
                    }
                    @Override
                    public void handleRecovery(Recoverable recoverable) {
                        start();
                    }
                });
            } catch (Exception e) {

                connection = null;
                return null;
            }
        }

        return connection;
    }

    public void start() {
        if (startRunning)
            return;

        try {
            if (connectionFactory != null) {
                Connection conn = tryGetConnection();
                if (conn != null) {
                    this.startPublisher(conn);
                    this.startWorker(conn);
                } else {
                    // 重新启动
                    startAgainTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 5 * 1000);
                }
            }
        } finally {
            startRunning = false;
        }
    }

    // 开始发布
    private void startPublisher(Connection connection) {
        if (connection != null) {
            if (pubChannel != null && pubChannel.isOpen()) {
                try {
                    pubChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                pubChannel = connection.createChannel();

                String queue = this.publishToQueue; // queue name
                boolean durable = true; // durable - RabbitMQ will never lose the queue if a crash occurs
                boolean exclusive = false; // exclusive - if queue only will be used by one connection
                boolean autoDelete = false; // autodelete - queue is deleted when last consumer unsubscribes
                // 声明队列
                AMQP.Queue.DeclareOk qOK = pubChannel.queueDeclare(queue, durable, exclusive, autoDelete, null);

                while (resendQueue.size() > 0) {
                    String[] m = resendQueue.poll();
                    if (m != null && m.length > 0) {
                        publish(m[0], m[1], m[2]);
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 发布
    private boolean publish(String exchangeName, String routingKey, String message) {
        boolean ok = false;

        try {
            pubChannel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            ok = true;
        } catch (Exception e) {
            resendQueue.add(new String[]{exchangeName, routingKey, message});
        }
        return ok;
    }


    private void startWorker(Connection connection) {
        if (this.retryWorkerRunning)
            return;

        try {
            if (connection != null) {
                final Channel resumeChannel = connection.createChannel();

                String queueName = this.consumeFromQueue; // queue name

                DefaultConsumer dc = new DefaultConsumer(resumeChannel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String routingKey = envelope.getRoutingKey();
                        String contentType = properties.getContentType();

                        long deliveryTag = envelope.getDeliveryTag();


                        boolean workOK = work(body);
                        if (workOK) {
                            resumeChannel.basicAck(deliveryTag, false);
                        } else {
                            resumeChannel.basicReject(deliveryTag, true);
                        }
                    }
                };

                boolean autoAck = false;
                // 取消自动ack
                resumeChannel.basicConsume(queueName, autoAck, dc);


            }
        } catch (Exception e) {

            this.retryWorkerTimer.schedule(new TimerTask() {
                public void run() {
                    startWorker(MQUtil.this.connection);
                }
            }, 5 * 1000);
        } finally {
            retryWorkerRunning = false;
        }
    }

    /*

    处理接收到的消息。子类需要重写本方法以便实现自已的处理逻辑，本方法默认只作为log输出使用！
    特别注意：本方法一旦返回false则消息将被MQ服务器重新放入队列， 请一定注意false是你需要的，不然消息会重复哦。

    参数：
    contentBody - 从MQ服务器取到的消息内容byte数组
    返回：
    true表示此消息处理成功(本类将回复ACK给MQ服务端，服务端队列中会交此消息正常删除)， 否则不成功(本类将通知MQ服务器将该条消息重新放回队列，以备下次再次获取)。
    */


    private boolean work(byte[] contentBody) {
        try {
            String msg = new String(contentBody, StandardCharsets.UTF_8);

            return true;
        } catch (Exception e) {

            return true;
        }
    }
}
