package mmp.im;

import mmp.im.client.connector.ClientToGateConnector;
import mmp.im.common.server.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.cache.acknowledge.ResendMessageThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TCPGateApplication implements CommandLineRunner {


    @Autowired
    private ClientToGateConnector clientToGateConnector;


    @Autowired
    private ResendMessageMap resendMessageMap;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(TCPGateApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        new Thread(() -> clientToGateConnector.connect()).start();


        LOG.warn("starting ResendMessageThread... ");

        new Thread(new ResendMessageThread(resendMessageMap), "ResendMessageThread").start();

        LOG.warn("Spring Boot 启动完成");
    }


}

