package mmp.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebsocketGateApplication implements CommandLineRunner {


    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(WebsocketGateApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {


        LOG.warn("Spring Boot 启动完成");
    }


}

