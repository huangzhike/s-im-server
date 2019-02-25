package mmp.im;

import mmp.im.common.util.mq.MQConsumer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "mmp.im")
@MapperScan("mmp.im.logic.database.dao")
@EnableCaching(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class LogicApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private MQConsumer mqConsumer;

    public static void main(String[] args) {
        SpringApplication.run(LogicApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        mqConsumer.start();

        LOG.warn("Spring Boot 启动完成");
    }


}

