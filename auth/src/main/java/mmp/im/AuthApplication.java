package mmp.im;

import mmp.im.auth.server.accept.GateToAuthAcceptor;
import mmp.im.common.server.tcp.cache.acknowledge.ResendMessageThread;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("mmp.im.auth.dao")
@EnableCaching(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class AuthApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private GateToAuthAcceptor gateToAuthAcceptor;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        new Thread(() -> gateToAuthAcceptor.bind()).start();

        new Thread(new ResendMessageThread(), "ackTimeoutScanner").start();

        LOG.warn("Spring Boot 启动完成");
    }


}

