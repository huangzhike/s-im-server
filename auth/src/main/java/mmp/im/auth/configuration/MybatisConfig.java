package mmp.im.auth.configuration;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        // 设置驼峰命名规则
        return (configuration) -> configuration.setMapUnderscoreToCamelCase(true);
    }
}
