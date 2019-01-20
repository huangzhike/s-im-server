package mmp.im.gate;

import mmp.im.common.server.tcp.cache.ack.ResendMessageThread;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication

@MapperScan("mmp.im.logic.dao")
@EnableCaching(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class TCPGateApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(TCPGateApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        Thread t = new Thread(new ResendMessageThread(), "ack.timeout.scanner");
        t.setDaemon(true);
        t.start();
        LOG.warn("Spring Boot 启动完成");
    }


}

