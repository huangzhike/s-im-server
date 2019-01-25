package mmp.im.gate;

import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageMap;
import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageThread;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DistributeApplication implements CommandLineRunner {

    @Autowired
    private GateToDistAcceptor gateToDistAcceptor;


    @Autowired
    private ResendMessageMap resendMessageMap;


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(DistributeApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        new Thread(() -> gateToDistAcceptor.bind()).start();

        new Thread(new ResendMessageThread(resendMessageMap), "ResendMessageThread").start();

        LOG.warn("Spring Boot 启动完成");
    }


}

