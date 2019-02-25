package mmp.im;

import im.database.service.FriendMessageService;
import mmp.im.common.util.spring.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("mmp.im.auth.database.dao")
@EnableCaching(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class AuthApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        LOG.warn("Spring Boot 启动完成");



    }


}

