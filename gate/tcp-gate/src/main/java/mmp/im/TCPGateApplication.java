package mmp.im;

import mmp.im.common.server.message.ResendMessageThread;
import mmp.im.gate.acceptor.ClientToGateAcceptor;
import mmp.im.gate.connector.GateToDistConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "mmp.im")
public class TCPGateApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${gateToDistAcceptor.connect.port}")
    private Integer gateToDistAcceptorPort;

    @Value("${gateToDistConnector.connect.host}")
    private String gateToDistConnectorHost;

    @Value("${clientToGateAcceptor.bind.port}")
    private Integer clientToGateAcceptorPort;

    public static void main(String[] args) {
        SpringApplication.run(TCPGateApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        new Thread(() -> new GateToDistConnector(gateToDistConnectorHost, gateToDistAcceptorPort).connect()).start();

        new Thread(() -> new ClientToGateAcceptor(clientToGateAcceptorPort).bind()).start();

        new Thread(new ResendMessageThread(), "ResendMessageThread").start();

        LOG.warn("Spring Boot 启动完成");
    }


}

