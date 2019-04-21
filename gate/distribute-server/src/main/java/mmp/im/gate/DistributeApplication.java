package mmp.im.gate;

import mmp.im.common.server.message.ResendMessageThread;
import mmp.im.common.util.mq.MQProducer;
import mmp.im.gate.acceptor.GateToDistAcceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("mmp.im.gate.database.dao")
@SpringBootApplication(scanBasePackages = "mmp.im")
public class DistributeApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${gateToDistAcceptor.bind.port}")
    private Integer gateToDistAcceptorPort;

    @Autowired
    private MQProducer mqProducer;


    public static void main(String[] args) {
        SpringApplication.run(DistributeApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        new Thread(() -> new GateToDistAcceptor(gateToDistAcceptorPort).bind()).start();

        new Thread(new ResendMessageThread(), "ResendMessageThread").start();

        mqProducer.start();

        LOG.warn("Spring Boot 启动完成");
    }


}

