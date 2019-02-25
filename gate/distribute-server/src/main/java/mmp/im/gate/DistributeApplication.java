package mmp.im.gate;

import mmp.im.common.server.cache.acknowledge.ResendMessageThread;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("mmp.im.gate.database.dao")
@SpringBootApplication(scanBasePackages = "mmp.im")
public class DistributeApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private GateToDistAcceptor gateToDistAcceptor;
    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private ResendMessageThread resendMessageThread;

    public static void main(String[] args) {
        SpringApplication.run(DistributeApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        new Thread(() -> gateToDistAcceptor.bind()).start();

        new Thread(resendMessageThread, "ResendMessageThread").start();

        mqProducer.start();

        LOG.warn("Spring Boot 启动完成");
    }


}

