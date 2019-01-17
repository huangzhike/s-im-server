package mmp.im.logic;

import mmp.im.logic.util.MQProcessor;
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
public class LogicApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(LogicApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        MQProcessor mq = new MQProcessor("", "");
        mq.start();
        LOG.warn("Spring Boot 启动完成");
    }


}

