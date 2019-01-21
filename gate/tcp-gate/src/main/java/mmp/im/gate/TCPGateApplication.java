package mmp.im.gate;

import mmp.im.common.server.tcp.cache.ack.ResendMessageThread;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.gate.server.accept.ClientToGateAcceptor;
import mmp.im.gate.server.connect.GateToAuthConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication

@EnableCaching(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class TCPGateApplication implements CommandLineRunner {


    @Value("${gateToAuthConnector.connect.host}")
    private String gateToAuthConnectorHost;

    @Value("${gateToAuthAcceptor.connect.port}")
    private Integer gateToAuthAcceptorPort;

    @Value("${clientToGateAcceptor.bind.port}")
    private Integer clientToGateAcceptorPort;

    @Autowired
    private GateToAuthConnector gateToAuthConnector;

    // @Autowired
    // private ClientToGateAcceptor clientToGateAcceptor;

    @Autowired
    private MQProducer mqProducer;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(TCPGateApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {


        new Thread(() -> {
            try {
                gateToAuthConnector.connect(gateToAuthConnectorHost, gateToAuthAcceptorPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // new Thread(() -> {
        //     try {
        //         clientToGateAcceptor.bind(clientToGateAcceptorPort);
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // }).start();

        new Thread(new ResendMessageThread(), "ackTimeoutScanner").start();

        mqProducer.start();

        LOG.warn("Spring Boot 启动完成");
    }


}

